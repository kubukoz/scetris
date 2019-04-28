package com.kubukoz.scetris

import cats.effect.implicits._
import cats.effect.{Concurrent, ExitCode, IO, IOApp, Timer}
import cats.implicits._
import com.kubukoz.scetris.Main.{newRandomFigure, screen}
import com.kubukoz.scetris.components.GameState.FigureGenerator
import com.kubukoz.scetris.components._
import com.kubukoz.scetris.domain._
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen
import fs2.Stream
import fs2.concurrent.Queue

import scala.concurrent.duration.DurationLong
import scala.io.StdIn
import scala.swing.event.{Key, KeyPressed}
import scala.swing.{SimpleSwingApplication, _}
import scala.util.Random

object Main extends SimpleSwingApplication with IOApp {
  val screen = Screen.Default

  val newRandomFigure: FigureGenerator = {
    val allSingletons = Figure.Singletons.all

    IO(Random.nextInt(allSingletons.length)).map { index =>
      allSingletons(index)
    }
  }

  val mainFrame = new MainFrame

  override def top: Frame = {
    mainFrame.centerOnScreen()
    mainFrame.resizable = false
    mainFrame
  }

  override def main(args: Array[String]): Unit = super[IOApp].main(args)

  override def run(args: List[String]): IO[ExitCode] = {
    val runSwing = IO {
      super[SimpleSwingApplication].main(args.toArray)
    } >> GameCanvas.make.flatMap { canvas =>
      IO {
        canvas.preferredSize = canvas.calculateSize(screen)
        mainFrame.contents = canvas
      }.as(canvas)
    }

    runSwing.flatMap { canvas =>
      IO(StdIn.readLine("Press enter to close...\n")) race Game.instance(canvas, screen).flatMap(_.play)
    }.guarantee(IO(quit()))
  }.as(ExitCode.Success)

}

trait Game {
  def play: IO[Unit]
}

object Game {

  private val initialGameState: IO[GameState] =
    newRandomFigure
      .map(PositionedFigure.atInitialPosition(_, screen))
      .map(GameState(_, Map.empty, newRandomFigure, screen))

  private val moveDown = MoveCommand(Direction.Down)

  def instance(canvas: GameCanvas, screen: Screen)(implicit concurrent: Concurrent[IO], timer: Timer[IO]): IO[Game] =
    (initialGameState, Queue.bounded[IO, GameCommand](100)).mapN { (initial, events) =>
      new Game {
        private val setListeners: IO[Unit] = {
          IO {
            canvas.reactions += {
              case KeyPressed(_, DirectionKey(direction), _, _) =>
                events.enqueue1(MoveCommand(direction)).unsafeRunSync()
              case KeyPressed(_, RotationKey(rotation), _, _) =>
                events.enqueue1(RotateCommand(rotation)).unsafeRunSync()
              case KeyPressed(_, Key.Space, _, _) =>
                events.enqueue1(DropFigureCommand).unsafeRunSync()
            }
          }
        }.void

        val play: IO[Unit] = {
          val gravity = Stream.constant[IO, GameCommand](Game.moveDown).zipLeft(Stream.awakeEvery[IO](1.second))

          val env = new DrawingEnv(screen, canvas)

          val redrawStream =
            (gravity merge events.dequeue).evalScan(initial)(_ modifiedWith _).evalMap(canvas.drawState(_, env))

          val drawInitial = canvas.drawState(initial, env)

          val startGame = setListeners >> drawInitial >> redrawStream.compile.drain

          startGame
        }
      }
    }
}

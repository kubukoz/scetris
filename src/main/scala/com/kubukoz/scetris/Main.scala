package com.kubukoz.scetris

import cats.effect.{ExitCode, IO, IOApp}
import com.kubukoz.scetris.components.GameCanvas._
import com.kubukoz.scetris.components.GameState.FigureGenerator
import com.kubukoz.scetris.components._
import com.kubukoz.scetris.domain._
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen

import scala.concurrent.duration.DurationLong
import scala.io.StdIn
import scala.swing.{SimpleSwingApplication, _}
import scala.swing.event.{Key, KeyPressed}
import scala.util.Random
import fs2.Stream
import fs2.concurrent.Queue
import cats.implicits._
import cats.effect.implicits._

object Main extends SimpleSwingApplication with IOApp {
  val screen = Screen.Default

  val newRandomFigure: FigureGenerator = {
    val allSingletons = Figure.Singletons.all

    IO(Random.nextInt(allSingletons.length)).map { index =>
      allSingletons(index)
    }
  }

  val initialGameState =
    newRandomFigure.map(GameState(_, Map.empty, newRandomFigure, screen))

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
      IO(StdIn.readLine("Press enter to close...\n")) race handleEvents(canvas).compile.drain
    }.guarantee(IO(quit()))
  }.as(ExitCode.Success)

  def handleEvents(gameCanvas: GameCanvas): Stream[IO, Unit] = {
    val env = new DrawingEnv(screen, gameCanvas)

    val streamIO = Queue.bounded[IO, GameCommand](100).flatMap { events =>
      val eventSource = Stream
        .constant[IO, GameCommand](MoveCommand(Direction.Down))
        .zipLeft(Stream.awakeEvery[IO](1.second)) merge events.dequeue

      val refreshSource = Stream.eval(initialGameState).flatMap { initial =>
        eventSource.evalScan(initial)(_ modifiedWith _).evalMap(state => gameCanvas.drawState(state, env))
      }

      IO {
        gameCanvas.reactions += {
          case KeyPressed(_, DirectionKey(direction), _, _) =>
            events.enqueue1(MoveCommand(direction)).unsafeRunSync()
          case KeyPressed(_, RotationKey(rotation), _, _) =>
            events.enqueue1(RotateCommand(rotation)).unsafeRunSync()
          case KeyPressed(_, Key.Space, _, _) =>
            events.enqueue1(DropFigureCommand).unsafeRunSync()
        }
      }.as(refreshSource)
    }

    Stream.eval(streamIO).flatten
  }
}

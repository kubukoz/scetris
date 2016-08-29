package com.kubukoz.scetris

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy, ThrottleMode}
import com.kubukoz.scetris.components.GameCanvas._
import com.kubukoz.scetris.components.GameState.FigureGenerator
import com.kubukoz.scetris.components._
import com.kubukoz.scetris.domain._
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen

import scala.concurrent.duration.DurationLong
import scala.io.StdIn
import scala.swing._
import scala.swing.event.KeyPressed
import scala.util.Random

object Main extends SimpleSwingApplication {
  implicit val screen = Screen.Default

  implicit val newRandomFigure: FigureGenerator = { () => {
    val allSingletons = Figure.Singletons.all
    allSingletons(Random.nextInt(allSingletons.length))
  }
  }

  val initialGameState = GameState(newRandomFigure(), Map.empty)

  val canvas = GameCanvas

  override def top: Frame = new MainFrame {
    centerOnScreen()
    resizable = false
    contents = canvas
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
    canvas.preferredSize = calculateSize(screen)

    implicit val system = ActorSystem("system")
    implicit val mat = ActorMaterializer()

    val eventSource =
      Source.queue[GameEvent](bufferSize = 100, overflowStrategy = OverflowStrategy.backpressure)

    val refreshSource =
      Source.repeat[GameEvent](MoveEvent(Direction.Down))
        .throttle(1, 1.second, 1, ThrottleMode.Shaping)

    val updateState = Flow[GameEvent].scan(initialGameState)(_ modifiedWith _)
    val drawState = Sink.foreach[GameState](canvas.drawState)

    val eventQueue = eventSource.merge(refreshSource)
      .via(updateState)
      .to(drawState)
      .run()

    canvas.reactions += {
      case KeyPressed(_, DirectionKey(direction), _, _) =>
        eventQueue.offer(MoveEvent(direction))
      case KeyPressed(_, RotationKey(rotation), _, _) =>
        eventQueue.offer(RotateEvent(rotation))
    }

    StdIn.readLine("Press enter to close...\n")
    eventQueue.complete()
    system.terminate()
    sys.exit(0)
  }
}
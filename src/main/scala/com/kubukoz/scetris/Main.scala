package com.kubukoz.scetris

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy, ThrottleMode}
import com.kubukoz.scetris.components.GameCanvas.FigureGenerator
import com.kubukoz.scetris.components.{Figure, GameCanvas, RotationKey}
import com.kubukoz.scetris.domain.{Direction, DirectionKey, Offset, Rotation}
import com.kubukoz.scetris.meta.Config.Screen

import scala.concurrent.duration.DurationLong
import scala.io.StdIn
import scala.swing._
import scala.swing.event.KeyPressed
import scala.util.Random

object Main extends SimpleSwingApplication {
  implicit val newRandomFigure: FigureGenerator = { () => {
    val tempFigure = Figure.Types(Random.nextInt(Figure.Types.length))
      .copy(leftTop = Offset.origin)

    val startingOffset = Offset((Screen.width - tempFigure.width) / 2, 0)

    tempFigure.copy(leftTop = startingOffset)
  }
  }

  val canvas = new GameCanvas

  override def top: Frame = new MainFrame {
    centerOnScreen()
    resizable = false
    contents = canvas
    canvas.focusable = true
    canvas.requestFocus()
    canvas.listenTo(canvas.keys)
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)

    implicit val system = ActorSystem("system")
    implicit val mat = ActorMaterializer()

    val eventSource =
      Source.queue[GameEvent](bufferSize = 100, overflowStrategy = OverflowStrategy.backpressure)

    val refreshSource =
      Source.repeat[GameEvent](MoveEvent(Direction.Down))
        .throttle(1, 1.second, 1, ThrottleMode.Shaping)

    val updateState = Flow[GameEvent].scan(GameState())(_ modifiedWith _)
    val drawState = Sink.foreach[GameState](_.draw())

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

trait GameEvent

case class MoveEvent(direction: Direction) extends GameEvent

case class RotateEvent(rotation: Rotation) extends GameEvent

case class GameState(value: Int = 0) {
  def modifiedWith(event: GameEvent) = {
    println(s"(returning modified state $this+1)")
    copy(value + 1)
  }

  def draw() = {
    println("(drawing state)")
  }
}
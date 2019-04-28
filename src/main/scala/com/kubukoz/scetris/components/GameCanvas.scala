package com.kubukoz.scetris.components

import java.awt.{Dimension, Graphics2D}

import cats.effect.IO
import com.kubukoz.scetris.drawable.{Drawable, DrawingEnv}
import com.kubukoz.scetris.meta.Config._
import cats.implicits._

import scala.swing.Component

trait GameCanvas extends Component {
  def calculateSize(screen: Screen): Dimension
  def drawState(state: GameState, env: DrawingEnv): IO[Unit]
}

object GameCanvas {

  val make: IO[GameCanvas] = IO {
    new GameCanvas {
      var drawable: Option[Drawable] = None

      focusable = true
      requestFocus()
      listenTo(keys)

      override protected def paintComponent(g: Graphics2D): Unit = {
        drawable.traverse_(_.execute(g)).unsafeRunSync()
      }

      override def drawState(state: GameState, env: DrawingEnv): IO[Unit] = IO {
        drawable = Some(state.draw(env))
        repaint()
      }

      override def calculateSize(screen: Screen): Dimension = new Dimension(
        gridSize * screen.width + gridBorder * (screen.width - 1),
        gridSize * screen.height + gridBorder * (screen.height - 1)
      )
    }
  }
}

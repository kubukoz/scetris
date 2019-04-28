package com.kubukoz.scetris.drawable

import java.awt.Color

import cats.effect.IO
import com.kubukoz.scetris.components.PositionedFigure
import com.kubukoz.scetris.domain.Position
import com.kubukoz.scetris.meta.Config._

import scala.swing.{Color, Graphics2D}
import cats.implicits._

case class GameStateDrawable(figure: PositionedFigure, placedBlocks: Map[Position, Color], env: DrawingEnv)
    extends Drawable {
  protected def drawHorizontalLines(g: Graphics2D): IO[Unit] = {
    (1 until env.screen.height).toStream.traverse_ { rowIndex =>
      val y = rowIndex * gridSize + gridBorder * (rowIndex - 1)

      IO { g.fillRect(0, y, env.canvas.size.width, gridBorder) }
    }
  }

  protected def drawVerticalLines(g: Graphics2D): IO[Unit] = {
    (1 until env.screen.width).toStream.traverse_ { columnIndex =>
      val x = columnIndex * gridSize + gridBorder * (columnIndex - 1)
      IO { g.fillRect(x, 0, gridBorder, env.canvas.size.height) }
    }
  }

  override def execute(g: Graphics2D): IO[Unit] = {
    val setColor = IO(g.setColor(Color.LIGHT_GRAY))

    val drawBlocks = (placedBlocks ++ figure.positionMap).toList.traverse_ {
      case (position, color) =>
        IO(g.setColor(color)) >>
          position.toBlock.draw(env).execute(g)
    }

    setColor >>
      drawVerticalLines(g) >>
      drawHorizontalLines(g) >>
      drawBlocks
  }
}

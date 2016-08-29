package com.kubukoz.scetris.drawable

import java.awt.Color

import com.kubukoz.scetris.components.{Figure, GameCanvas}
import com.kubukoz.scetris.domain.Position
import com.kubukoz.scetris.meta.Config._

import scala.swing.{Color, Graphics2D}

case class GameStateDrawable(figure: Figure, placedFigures: Map[Position, Color])(implicit screen: Screen) extends Drawable {
  protected def drawHorizontalLines(g: Graphics2D): Unit = {
    (1 until screen.height).foreach { rowIndex =>
      val y = rowIndex * gridSize + gridBorder * (rowIndex - 1)
      g.fillRect(0, y, GameCanvas.size.width, gridBorder)
    }
  }

  protected def drawVerticalLines(g: Graphics2D): Unit = {
    (1 until screen.width).foreach { columnIndex =>
      val x = columnIndex * gridSize + gridBorder * (columnIndex - 1)
      g.fillRect(x, 0, gridBorder, GameCanvas.size.height)
    }
  }

  override def execute(g: Graphics2D): Unit = {
    g.setColor(Color.LIGHT_GRAY)
    drawVerticalLines(g)
    drawHorizontalLines(g)

    (placedFigures ++ figure.toMap).foreach { case (position, color) =>
      g.setColor(color)
      position.toBlock.draw.execute(g)
    }
  }
}

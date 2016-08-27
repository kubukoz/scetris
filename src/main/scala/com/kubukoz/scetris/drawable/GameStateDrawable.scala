package com.kubukoz.scetris.drawable

import java.awt.Color

import com.kubukoz.scetris.components.{Figure, GameCanvas}

import scala.swing._

case class GameStateDrawable(figure: Figure, placedFigures: Set[Figure]) extends Drawable {

  import com.kubukoz.scetris.meta.Config.Screen._
  import com.kubukoz.scetris.meta.Config._

  protected def drawHorizontalLines(g: Graphics2D): Unit = {
    (1 until height).foreach { rowIndex =>
      val y = rowIndex * gridSize + gridBorder * (rowIndex - 1)
      g.fillRect(0, y, GameCanvas.size.width, gridBorder)
    }
  }

  protected def drawVerticalLines(g: Graphics2D): Unit = {
    (1 until width).foreach { columnIndex =>
      val x = columnIndex * gridSize + gridBorder * (columnIndex - 1)
      g.fillRect(x, 0, gridBorder, GameCanvas.size.height)
    }
  }

  override def execute(g: Graphics2D): Unit = {
    g.setColor(Color.LIGHT_GRAY)
    drawVerticalLines(g)
    drawHorizontalLines(g)

    (placedFigures + figure).foreach(_.draw.execute(g))
  }
}

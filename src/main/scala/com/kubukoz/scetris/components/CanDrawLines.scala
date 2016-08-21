package com.kubukoz.scetris.components

import com.kubukoz.scetris.meta.Config.Screen._
import com.kubukoz.scetris.meta.Config._

import scala.swing.{Component, _}

trait CanDrawLines {
  self: Component =>

  protected def drawHorizontalLines(g: Graphics2D): Unit = {
    (1 until height).foreach { rowIndex =>
      val y = rowIndex * gridSize + gridBorder * (rowIndex - 1)
      g.fillRect(0, y, size.width, gridBorder)
    }
  }

  protected def drawVerticalLines(g: Graphics2D): Unit = {
    (1 until width).foreach { columnIndex =>
      val x = columnIndex * gridSize + gridBorder * (columnIndex - 1)

      g.fillRect(x, 0, gridBorder, size.height)
    }
  }
}

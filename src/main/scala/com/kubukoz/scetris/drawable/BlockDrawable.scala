package com.kubukoz.scetris.drawable

import com.kubukoz.scetris.meta.Config._

import scala.swing._

case class BlockDrawable(x: Int, y: Int)(implicit screen: Screen) extends Drawable {
  override def execute(g: Graphics2D): Unit = {
    val pixelX = gridSize * x + gridBorder * x
    val pixelY = gridSize * y + gridBorder * y
    g.fillRect(pixelX, pixelY, gridSize, gridSize)
  }
}

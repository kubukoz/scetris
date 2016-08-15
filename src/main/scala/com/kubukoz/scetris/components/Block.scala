package com.kubukoz.scetris.components

import com.kubukoz.scetris.meta.Config._

import scala.swing.Graphics2D

case class Block(x: Int, y: Int) {
  def draw(g: Graphics2D): Unit = {
    val pixelX = gridSize * x + gridBorder * x
    val pixelY = gridSize * y + gridBorder * y
    g.fillRect(pixelX, pixelY, gridSize, gridSize)
  }
}

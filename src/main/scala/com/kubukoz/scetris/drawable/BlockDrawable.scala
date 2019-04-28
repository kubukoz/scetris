package com.kubukoz.scetris.drawable

import cats.effect.IO
import com.kubukoz.scetris.meta.Config._

import scala.swing._

case class BlockDrawable(x: Int, y: Int) extends Drawable {
  override def execute(g: Graphics2D): IO[Unit] = {
    val pixelX = gridSize * x + gridBorder * x
    val pixelY = gridSize * y + gridBorder * y

    IO { g.fillRect(pixelX, pixelY, gridSize, gridSize) }
  }
}

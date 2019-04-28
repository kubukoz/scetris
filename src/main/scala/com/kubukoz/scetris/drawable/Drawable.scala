package com.kubukoz.scetris.drawable

import cats.effect.IO

import scala.swing._

abstract class Drawable {
  def execute(g: Graphics2D): IO[Unit]
}

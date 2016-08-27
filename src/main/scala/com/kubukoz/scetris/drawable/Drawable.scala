package com.kubukoz.scetris.drawable

import scala.swing._

trait Drawable {
  def execute(g: Graphics2D): Unit
}

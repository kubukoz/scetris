package com.kubukoz.scetris.components

import scala.swing._

trait Drawable {
  def draw: Traversable[DrawEvent]
}

trait DrawEvent {
  def execute(g: Graphics2D): Unit
}
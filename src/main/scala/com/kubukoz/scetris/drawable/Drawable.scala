package com.kubukoz.scetris.drawable

import com.kubukoz.scetris.meta.Config.Screen

import scala.swing._

abstract class Drawable(implicit screen: Screen) {
  def execute(g: Graphics2D): Unit
}

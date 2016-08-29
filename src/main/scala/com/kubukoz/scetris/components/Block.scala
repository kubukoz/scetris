package com.kubukoz.scetris.components

import com.kubukoz.scetris.drawable.{BlockDrawable, CanDraw, Drawable}
import com.kubukoz.scetris.meta.Config.Screen

case class Block(x: Int, y: Int) extends CanDraw {
  def draw(implicit screen: Screen): Drawable = BlockDrawable(x, y)
}
package com.kubukoz.scetris.components

import com.kubukoz.scetris.drawable.{BlockDrawable, CanDraw, Drawable}

case class Block(x: Int, y: Int) extends CanDraw {
  def draw: Drawable = BlockDrawable(x, y)
}
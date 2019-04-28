package com.kubukoz.scetris.components

import com.kubukoz.scetris.drawable.{BlockDrawable, CanDraw, Drawable, DrawingEnv}

case class Block(x: Int, y: Int) extends CanDraw {
  def draw(env: DrawingEnv): Drawable = BlockDrawable(x, y)
}

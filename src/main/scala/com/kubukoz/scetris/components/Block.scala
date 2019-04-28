package com.kubukoz.scetris.components

import com.kubukoz.scetris.drawable.{BlockDrawable, CanDraw, Drawable, DrawingEnv}
import com.kubukoz.scetris.meta.Config.Screen

case class Block(x: Int, y: Int) extends CanDraw {
  def draw(env: DrawingEnv): Drawable = BlockDrawable(x, y)
}

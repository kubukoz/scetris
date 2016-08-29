package com.kubukoz.scetris.drawable

import com.kubukoz.scetris.meta.Config.Screen

trait CanDraw {
  def draw(implicit screen: Screen): Drawable
}

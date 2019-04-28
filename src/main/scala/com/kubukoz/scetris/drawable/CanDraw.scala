package com.kubukoz.scetris.drawable

import com.kubukoz.scetris.components.GameCanvas
import com.kubukoz.scetris.meta.Config.Screen

class DrawingEnv(val screen: Screen, val canvas: GameCanvas)

trait CanDraw {
  def draw(env: DrawingEnv): Drawable
}

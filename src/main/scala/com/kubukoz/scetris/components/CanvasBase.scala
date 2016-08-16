package com.kubukoz.scetris.components

import scala.swing.Component

trait CanvasBase {
  self: Component =>
  preferredSize = GameCanvas.calculateSize
  focusable = true
  requestFocus()
  listenTo(keys)
}

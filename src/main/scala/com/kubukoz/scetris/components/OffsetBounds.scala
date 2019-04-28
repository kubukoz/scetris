package com.kubukoz.scetris.components

case class OffsetBounds(x: Bound, y: Bound) {
  def height: Int = (y.max - y.min).abs + 1
  def width: Int  = (x.max - x.min).abs + 1
}

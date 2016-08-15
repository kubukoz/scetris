package com.kubukoz.scetris.domain

case class Offset(x: Int, y: Int) {
  def rotated: Offset = Offset(-y, x)
}
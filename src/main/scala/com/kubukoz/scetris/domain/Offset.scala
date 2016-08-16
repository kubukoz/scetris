package com.kubukoz.scetris.domain

case class Offset(x: Int, y: Int) {
  def rotated(rotation: Rotation): Offset = rotation match{
    case RightRotation => Offset(-y, x)
    case LeftRotation => Offset(y, -x)
  }
}
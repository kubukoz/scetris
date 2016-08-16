package com.kubukoz.scetris.domain

import com.kubukoz.scetris.components.Block

case class Offset(x: Int, y: Int) {
  /**
    * Returns a new offset, rotated in the chosen direction.
    **/
  def rotated(rotation: Rotation): Offset = rotation match {
    case RightRotation => Offset(-y, x)
    case LeftRotation => Offset(y, -x)
  }

  /**
    * Returns an absolute position - a block
    * */
  def toPosition(center: Offset): Block = Block(center.x + x, center.y + y)
}
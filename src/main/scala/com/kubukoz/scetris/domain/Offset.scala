package com.kubukoz.scetris.domain

import com.kubukoz.scetris.components.Block

case class Position(x: Int, y: Int) {
  def toBlock: Block              = Block(x, y)
  def moved(move: Move): Position = Position(x + move.Δx, y + move.Δy)
}

case class Offset(x: Int, y: Int) {

  /**
    * Returns a new offset, rotated in the chosen direction.
    **/
  def rotated(rotation: Rotation): Offset = rotation match {
    case RightRotation => Offset(-y, x)
    case LeftRotation  => Offset(y, -x)
  }

  /**
    * Returns an absolute position - a block
    **/
  def toPosition(center: Offset): Position = Position(center.x + x, center.y + y)
}

object Offset {
  val origin         = Offset(0, 0)
  val originPosition = Position(0, 0)
}

package com.kubukoz.scetris.domain

import scala.swing.event.Key

sealed case class Direction(x: Int, y: Int)

object Direction {
  val Left = new Direction(-1, 0)
  val Right = new Direction(1, 0)
  val Down = new Direction(0, 1)
}

/**
  * Extractor for converting a keyboard press to a [[Direction]].
  **/
object DirectionKey {

  import Direction._

  val toKey: PartialFunction[Key.Value, Direction] = {
    case Key.Left => Left
    case Key.Right => Right
    case Key.Down => Down
  }

  def unapply(key: Key.Value): Option[Direction] = toKey.lift(key)
}
package com.kubukoz.scetris.domain

import cats.kernel.CommutativeMonoid

import scala.swing.event.Key

case class Move(Δx: Int, Δy: Int)

object Move {

  val fromDirection: Direction => Move = {
    case Direction(dx, dy) => Move(dx.toInt, dy.toInt)
  }

  implicit val monoid: CommutativeMonoid[Move] = new CommutativeMonoid[Move] {
    override def empty: Move = Move(0, 0)

    override def combine(x: Move, y: Move): Move = Move(x.Δx + y.Δx, x.Δy + y.Δy)
  }
}

sealed trait Delta extends Product with Serializable {

  val toInt: Int = this match {
    case Delta.Negative => -1
    case Delta.Identity => 0
    case Delta.Positive => 1
  }
}

object Delta {
  case object Negative extends Delta
  case object Identity extends Delta
  case object Positive extends Delta
}

sealed abstract case class Direction(x: Delta, y: Delta)

object Direction {
  val Left: Direction  = new Direction(Delta.Negative, Delta.Identity) {}
  val Right: Direction = new Direction(Delta.Positive, Delta.Identity) {}
  val Down: Direction  = new Direction(Delta.Identity, Delta.Positive) {}
}

/**
  * Extractor for converting a keyboard press to a [[Direction]].
  **/
object DirectionKey {

  import Direction._

  val toKey: PartialFunction[Key.Value, Direction] = {
    case Key.Left  => Left
    case Key.Right => Right
    case Key.Down  => Down
  }

  def unapply(key: Key.Value): Option[Direction] = toKey.lift(key)
}

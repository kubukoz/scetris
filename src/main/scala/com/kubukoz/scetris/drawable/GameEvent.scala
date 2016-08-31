package com.kubukoz.scetris.drawable

import com.kubukoz.scetris.domain.{Direction, Rotation}

trait GameEvent

case class MoveEvent(direction: Direction) extends GameEvent

case class RotateEvent(rotation: Rotation) extends GameEvent

case object DropFigureEvent extends GameEvent
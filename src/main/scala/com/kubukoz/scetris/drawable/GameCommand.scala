package com.kubukoz.scetris.drawable

import com.kubukoz.scetris.domain.{Direction, Rotation}

trait GameCommand

case class MoveCommand(direction: Direction) extends GameCommand

case class RotateCommand(rotation: Rotation) extends GameCommand

case object DropFigureCommand extends GameCommand
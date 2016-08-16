package com.kubukoz.scetris.domain

sealed trait Rotation
case object LeftRotation extends Rotation
case object RightRotation extends Rotation
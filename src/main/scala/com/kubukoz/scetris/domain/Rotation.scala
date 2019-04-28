package com.kubukoz.scetris.domain

import scala.swing.event.Key

sealed trait Rotation extends Product with Serializable

case object LeftRotation extends Rotation

case object RightRotation extends Rotation

object RotationKey {

  def unapply(key: Key.Value): Option[Rotation] = key match {
    case Key.E => Some(RightRotation)
    case Key.Q => Some(LeftRotation)
    case _     => None
  }
}

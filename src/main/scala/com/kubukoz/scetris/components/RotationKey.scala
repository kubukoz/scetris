package com.kubukoz.scetris.components

import com.kubukoz.scetris.domain.{LeftRotation, RightRotation, Rotation}

import scala.swing.event.Key

object RotationKey {
  def unapply(key: Key.Value): Option[Rotation] = key match {
    case Key.E => Some(RightRotation)
    case Key.Q => Some(LeftRotation)
    case _ => None
  }
}

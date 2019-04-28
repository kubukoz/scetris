package com.kubukoz.scetris.components

import java.awt.Color

import cats.implicits._
import com.kubukoz.scetris.components.Figure._
import com.kubukoz.scetris.domain.Offset.origin
import com.kubukoz.scetris.domain.{Offset, Rotation}

sealed abstract case class Figure(offsets: Set[Offset], color: Color) {

  lazy val offsetBounds: OffsetBounds =
    OffsetBounds(
      Bound(minXOffset(offsets), maxXOffset(offsets)),
      Bound(minYOffset(offsets), maxYOffset(offsets))
    )

  def mirror: Figure                      = new Figure(offsets.map(off => off.copy(x = -off.x)), color) {}
  def rotated(rotation: Rotation): Figure = new Figure(offsets.map(_.rotated(rotation)), color)         {}
  def withColor(color: Color): Figure     = new Figure(offsets, color)                                  {}
}

object Figure {
  private def minXOffset(elems: Set[Offset]): Int = elems.map(_.x).toStream.minimumOption.combineAll
  private def maxXOffset(elems: Set[Offset]): Int = elems.map(_.x).toStream.maximumOption.combineAll
  private def minYOffset(elems: Set[Offset]): Int = elems.map(_.y).toStream.minimumOption.combineAll
  private def maxYOffset(elems: Set[Offset]): Int = elems.map(_.y).toStream.maximumOption.combineAll

  object Singletons {
    val Z: Figure         = new Figure(Set(Offset(-1, -1), Offset(0, -1), origin, Offset(1, 0)), Color.CYAN) {}
    val S: Figure         = Z.mirror.withColor(Color.GREEN)
    val L: Figure         = new Figure(Set(Offset(-1, -1), Offset(-1, 0), origin, Offset(1, 0)), Color.MAGENTA) {}
    val J: Figure         = L.mirror.withColor(Color.LIGHT_GRAY)
    val I: Figure         = new Figure((-1 to 2).map(Offset(_, 0)).toSet, Color.RED) {}
    val O: Figure         = new Figure(Set(Offset(-1, -1), Offset(0, -1), Offset(-1, 0), Offset(0, 0)), Color.BLUE) {}
    val T: Figure         = new Figure(Set(Offset(-1, 0), Offset(0, 0), Offset(1, 0), Offset(0, 1)), Color.ORANGE) {}
    val all: List[Figure] = List(Z, S, L, J, I, O, T)
  }
}

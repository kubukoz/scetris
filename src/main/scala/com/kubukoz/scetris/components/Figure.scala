package com.kubukoz.scetris.components

import java.awt.Color

import com.kubukoz.scetris.components.Figure._
import com.kubukoz.scetris.domain.{Offset, Rotation}

import scala.swing._

object Figure {
  private def minXOffset(elems: Set[Offset]) = elems.map(_.x).min

  private def minYOffset(elems: Set[Offset]) = elems.map(_.y).min
}

sealed case class Figure(leftTop: Offset, offsets: Set[Offset]) {
  private val center =
    Offset(
      leftTop.x - minXOffset(offsets),
      leftTop.y - minYOffset(offsets)
    )

  def draw(g: Graphics2D): Unit = {
    val blocks = offsets.map(_.toPosition(center))

    blocks foreach (_.draw(g))
  }

  def rotated(rotation: Rotation) = {
    val newOffsets = offsets.map(_.rotated(rotation))
    val newLeftTop = Offset(
      center.x + minXOffset(newOffsets),
      center.y + minYOffset(newOffsets)
    )

    copy(newLeftTop, newOffsets)
  }
}

class ZFigure(leftTop: Offset)
  extends Figure(leftTop, Set(Offset(-1, -1), Offset(0, -1), Offset(0, 0), Offset(1, 0)))

class LFigure(leftTop: Offset)
  extends Figure(leftTop, Set(Offset(-1, -1), Offset(-1, 0), Offset(0, 0), Offset(1, 0)))
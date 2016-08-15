package com.kubukoz.scetris.components

import com.kubukoz.scetris.domain.Offset

import scala.swing._

sealed case class Figure(leftTop: Offset, offsets: Set[Offset]) {
  private val centerX = leftTop.x - offsets.map(_.x).min
  private val centerY = leftTop.y - offsets.map(_.y).min

  def draw(g: Graphics2D): Unit = {
    val blocks = offsets.map { offset =>
      Block(centerX + offset.x, centerY + offset.y)
    }

    blocks foreach (_.draw(g))
  }

  def rotatedRight = copy(
    offsets = offsets.map(_.rotated)
  )
}

class ZFigure(leftTop: Offset)
  extends Figure(leftTop, Set(Offset(-1, -1), Offset(0, -1), Offset(0, 0), Offset(1, 0)))
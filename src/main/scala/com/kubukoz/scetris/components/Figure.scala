package com.kubukoz.scetris.components

import com.kubukoz.scetris.domain.Offset

import scala.swing._

sealed case class Figure(position: Offset, offsets: Set[Offset]) {
  def draw(g: Graphics2D): Unit = {
    val blocks = offsets.map { offset =>
      Block(position.x + offset.x, position.y + offset.y)
    }

    blocks foreach (_.draw(g))
  }
}

object ZFigure extends Figure(
  position = Offset(1, 1),
  offsets = Set(
    Offset(0, 0),
    Offset(1, 0),
    Offset(1, 1),
    Offset(2, 1)
  )
)
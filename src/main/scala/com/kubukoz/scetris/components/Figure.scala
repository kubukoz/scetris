package com.kubukoz.scetris.components

import java.awt.Color

import com.kubukoz.scetris.components.Figure._
import com.kubukoz.scetris.domain.Offset.origin
import com.kubukoz.scetris.domain.{Direction, Offset, Rotation}
import com.kubukoz.scetris.meta.Config.Screen

import scala.swing.Graphics2D
import scala.util.Try

sealed case class Figure(leftTop: Offset, offsets: Set[Offset], color: Color) extends Drawable {
  def height = (maxYOffset(offsets) - minYOffset(offsets)).abs + 1

  def width = (maxXOffset(offsets) - minXOffset(offsets)).abs + 1

  def mirror = copy(offsets = offsets.map(off => off.copy(x = -off.x)))

  val center =
    Offset(
      leftTop.x - minXOffset(offsets),
      leftTop.y - minYOffset(offsets)
    )

  def canGoDown(placedFigures: Set[Figure]): Boolean = {
    val newPosition = copy(leftTop = leftTop.copy(y = leftTop.y + 1))

    newPosition.fitsFiguresAndScreen(placedFigures)
  }

  def fitsFiguresAndScreen(placedFigures: Set[Figure]): Boolean = {
    val positions = offsets.map(_.toPosition(center))

    lazy val fitsScreen = {
      val fitsScreenTop = leftTop.y >= 0
      val fitsScreenBottom = height + leftTop.y <= Screen.height
      val fitsScreenLeft = leftTop.x >= 0
      val fitsScreenRight = width + leftTop.x <= Screen.width
      fitsScreenBottom && fitsScreenTop && fitsScreenLeft && fitsScreenRight
    }

    //there is no piece that has common positions with this
    !placedFigures.exists { figure =>
      figure.offsets.map(_.toPosition(figure.center)) exists positions
    } && fitsScreen
  }

  override def draw: Traversable[DrawEvent] = List(DrawFigure(offsets.flatMap(_.toPosition(center).toBlock.draw), color))

  def moved(direction: Direction): Figure = copy(
    leftTop = leftTop.copy(
      leftTop.x + direction.x,
      leftTop.y + direction.y
    )
  )

  def rotated(rotation: Rotation) = {
    val newOffsets = offsets.map(_.rotated(rotation))
    val newLeftTop = Offset(
      center.x + minXOffset(newOffsets),
      center.y + minYOffset(newOffsets)
    )

    copy(newLeftTop, newOffsets)
  }
}

case class DrawFigure(blockDrawEvents: Traversable[DrawBlock], color: Color) extends DrawEvent {
  override def execute(g: Graphics2D): Unit = {
    g.setColor(color)
    blockDrawEvents.foreach(_.execute(g))
  }
}

object Figure {

  object Singletons {
    val Z = Figure(origin, Set(Offset(-1, -1), Offset(0, -1), origin, Offset(1, 0)), Color.CYAN)
    val S = Z.mirror.copy(color = Color.GREEN)
    val L = Figure(origin, Set(Offset(-1, -1), Offset(-1, 0), origin, Offset(1, 0)), Color.MAGENTA)
    val J = L.mirror.copy(color = Color.LIGHT_GRAY)
    val I = Figure(origin, (-1 to 2).map(Offset(_, 0)).toSet, Color.RED)
    val O = Figure(origin, Set(Offset(-1, -1), Offset(0, -1), Offset(-1, 0), Offset(0, 0)), Color.BLUE)
  }

  val Types = {
    import Singletons._
    List(Z, S, L, J, I, O)
  }

  private def minXOffset(elems: Set[Offset]): Int = Try(elems.map(_.x).min).getOrElse(0)

  private def maxXOffset(elems: Set[Offset]): Int = Try(elems.map(_.x).max).getOrElse(0)

  private def minYOffset(elems: Set[Offset]): Int = Try(elems.map(_.y).min).getOrElse(0)

  private def maxYOffset(elems: Set[Offset]): Int = Try(elems.map(_.y).max).getOrElse(0)
}

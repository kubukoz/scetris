package com.kubukoz.scetris.components

import java.awt.Color

import com.kubukoz.scetris.components.Figure._
import com.kubukoz.scetris.domain.Offset.{origin, originPosition}
import com.kubukoz.scetris.domain.{Direction, Offset, Position, Rotation}
import com.kubukoz.scetris.drawable.{CanDraw, Drawable, FigureDrawable}
import com.kubukoz.scetris.meta.Config.Screen

import scala.util.Try

sealed case class Figure(leftTop: Position, offsets: Set[Offset], color: Color) extends CanDraw {
  def height = (maxYOffset(offsets) - minYOffset(offsets)).abs + 1

  def width = (maxXOffset(offsets) - minXOffset(offsets)).abs + 1

  def mirror = copy(offsets = offsets.map(off => off.copy(x = -off.x)))

  val center =
    Offset(
      leftTop.x - minXOffset(offsets),
      leftTop.y - minYOffset(offsets)
    )

  def canGoDown(placedBlocks: Map[Position, Color])(implicit screen: Screen): Boolean = {
    moved(Direction.Down).fitsBlocksAndScreen(placedBlocks)
  }

  def fitsBlocksAndScreen(placedBlocks: Map[Position, Color])(implicit screen: Screen): Boolean = {
    val positions = offsets.map(_.toPosition(center))

    lazy val fitsScreen = {
      val fitsScreenBottom = height + leftTop.y <= screen.height
      val fitsScreenLeft = leftTop.x >= 0
      val fitsScreenRight = width + leftTop.x <= screen.width
      fitsScreenBottom && fitsScreenLeft && fitsScreenRight
    }

    //there is no piece that has common positions with this
    !(positions exists placedBlocks.keySet.contains) && fitsScreen
  }

  override def draw(implicit screen: Screen): Drawable = FigureDrawable(offsets.map(_.toPosition(center).toBlock), color)

  def moved(direction: Direction): Figure = copy(
    leftTop = leftTop.copy(
      leftTop.x + direction.x,
      leftTop.y + direction.y
    )
  )

  def rotated(rotation: Rotation) = {
    val newOffsets = offsets.map(_.rotated(rotation))
    val newLeftTop = Position(
      center.x + minXOffset(newOffsets),
      center.y + minYOffset(newOffsets)
    )

    copy(newLeftTop, newOffsets)
  }

  def toMap = offsets.map(_.toPosition(center)).map(_ -> color).toMap

}

object Figure {

  object Singletons {
    val Z = Figure(originPosition, Set(Offset(-1, -1), Offset(0, -1), origin, Offset(1, 0)), Color.CYAN)
    val S = Z.mirror.copy(color = Color.GREEN)
    val L = Figure(originPosition, Set(Offset(-1, -1), Offset(-1, 0), origin, Offset(1, 0)), Color.MAGENTA)
    val J = L.mirror.copy(color = Color.LIGHT_GRAY)
    val I = Figure(originPosition, (-1 to 2).map(Offset(_, 0)).toSet, Color.RED)
    val O = Figure(originPosition, Set(Offset(-1, -1), Offset(0, -1), Offset(-1, 0), Offset(0, 0)), Color.BLUE)
    val T = Figure(originPosition, Set(Offset(-1, 0), Offset(0, 0), Offset(1, 0), Offset(0, 1)), Color.ORANGE)
    val all = List(Z, S, L, J, I, O, T)
  }

  private def minXOffset(elems: Set[Offset]): Int = Try(elems.map(_.x).min).getOrElse(0)

  private def maxXOffset(elems: Set[Offset]): Int = Try(elems.map(_.x).max).getOrElse(0)

  private def minYOffset(elems: Set[Offset]): Int = Try(elems.map(_.y).min).getOrElse(0)

  private def maxYOffset(elems: Set[Offset]): Int = Try(elems.map(_.y).max).getOrElse(0)
}

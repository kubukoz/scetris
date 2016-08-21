package com.kubukoz.scetris.components

import java.awt.Color

import com.kubukoz.scetris.components.Figure._
import com.kubukoz.scetris.domain.Offset.origin
import com.kubukoz.scetris.domain.{Offset, Rotation}
import com.kubukoz.scetris.meta.Config.Screen

import scala.swing._

sealed case class Figure(leftTop: Offset, offsets: Set[Offset], color: Color) {

  def height = (maxYOffset(offsets) - minYOffset(offsets)).abs + 1

  def width = (maxXOffset(offsets) - minXOffset(offsets)).abs + 1

  def mirror = copy(offsets = offsets.map(off => off.copy(x = -off.x)))

  private val center =
    Offset(
      leftTop.x - minXOffset(offsets),
      leftTop.y - minYOffset(offsets)
    )

  def canGoDown(placedFigures: List[Figure]): Boolean = {
    val newPosition = copy(leftTop = leftTop.copy(y = leftTop.y + 1))

    newPosition.fitsFiguresAndScreen(placedFigures)
  }

  def fitsFiguresAndScreen(placedFigures: List[Figure]): Boolean = {
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

  def draw(g: Graphics2D): Unit = {
    val blocks = offsets.map(_.toPosition(center).toBlock)

    g.setColor(color)
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

  private def minXOffset(elems: Set[Offset]) = elems.map(_.x).min

  private def maxXOffset(elems: Set[Offset]) = elems.map(_.x).max

  private def minYOffset(elems: Set[Offset]) = elems.map(_.y).min

  private def maxYOffset(elems: Set[Offset]) = elems.map(_.y).max
}

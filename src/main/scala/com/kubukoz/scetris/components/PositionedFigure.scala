package com.kubukoz.scetris.components

import java.awt.Color

import cats.Eval
import com.kubukoz.scetris.domain.{Direction, Move, Offset, Position, Rotation}
import com.kubukoz.scetris.drawable.{CanDraw, Drawable, DrawingEnv, FigureDrawable}
import com.kubukoz.scetris.meta.Config.Screen

case class PositionedFigure(leftTop: Position, figure: Figure) extends CanDraw {

  def moved(move: Move): PositionedFigure = copy(
    leftTop = leftTop.moved(move)
  )

  def rotated(rotation: Rotation): PositionedFigure = {
    val newFigure = figure.rotated(rotation)

    val newLeftTop = Position(
      center.x + newFigure.offsetBounds.x.min,
      center.y + newFigure.offsetBounds.y.min
    )

    copy(newLeftTop, newFigure)
  }

  val center =
    Offset(
      leftTop.x - figure.offsetBounds.x.min,
      leftTop.y - figure.offsetBounds.y.min
    )

  override def draw(env: DrawingEnv): Drawable =
    FigureDrawable(figure.offsets.map(_.toPosition(center).toBlock), figure.color, env)

  def canGoDown(placedBlocks: Map[Position, Color], screen: Screen): Boolean = {
    moved(Move.fromDirection(Direction.Down)).fitsBlocksAndScreen(placedBlocks, screen)
  }

  def fitsBlocksAndScreen(placedBlocks: Map[Position, Color], screen: Screen): Boolean = {
    val fitsScreen = Eval.later {
      val fitsScreenBottom = figure.offsetBounds.height + leftTop.y <= screen.height
      val fitsScreenLeft   = leftTop.x >= 0
      val fitsScreenRight  = figure.offsetBounds.width + leftTop.x <= screen.width
      fitsScreenBottom && fitsScreenLeft && fitsScreenRight
    }

    //there is no piece that has common positions with this
    !(positions exists placedBlocks.keySet.contains) && fitsScreen.value
  }

  def positionMap: Map[Position, Color] = positions.map(_ -> figure.color).toMap

  private def positions: Set[Position] = figure.offsets.map(_.toPosition(center))
}

object PositionedFigure {

  def atInitialPosition(figure: Figure, screen: Screen): PositionedFigure = {
    PositionedFigure(Position((screen.width - figure.offsetBounds.width) / 2, 0), figure)
  }
}

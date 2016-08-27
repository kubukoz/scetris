package com.kubukoz.scetris.components

import com.kubukoz.scetris.domain.{Direction, Rotation}
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen

import scala.annotation.tailrec

final case class GameState(figure: Figure, placedFigures: Set[Figure])(implicit figureGenerator: () => Figure)
  extends CanDraw {

  def modifiedWith(event: GameEvent): GameState = event match {
    case MoveEvent(Direction.Down) => step
    case MoveEvent(direction) => moveFigure(direction)
    case RotateEvent(direction) => rotateFigure(direction)
  }

  override def draw: Drawable = GameStateDrawable(figure, placedFigures)

  protected def step: GameState = {
    if (figure.canGoDown(placedFigures))
      moveFigure(Direction.Down)
    else copy(
      figure = figureGenerator(),
      placedFigures = withoutCompleteRows(placedFigures + figure)
    )
  }

  @tailrec
  protected def withoutCompleteRows(currentFigures: Set[Figure]): Set[Figure] = {
    val figureOffsets = currentFigures.flatMap(fig => fig.offsets.map(_.toPosition(fig.center)))

    val rowsWithOffsets = figureOffsets.groupBy(_.y)

    val affectedRows = rowsWithOffsets.collect {
      case (y, matches) if matches.size == Screen.width => y
    }.toList

    val newFigures = currentFigures.map { fig =>
      val newOffsets = fig.offsets.filterNot(off => affectedRows.contains(off.toPosition(fig.center).y))

      fig.copy(offsets = newOffsets)
    }.filter(_.offsets.nonEmpty)

    if (affectedRows.isEmpty)
      currentFigures
    else
      withoutCompleteRows(newFigures)
  }

  protected def moveFigure(direction: Direction): GameState =
    replaceFigureIfPossible(figure.moved(direction))

  protected def rotateFigure(rotation: Rotation) =
    replaceFigureIfPossible(figure.rotated(rotation))

  protected def replaceFigureIfPossible(newFigure: Figure) = {
    if (newFigure.fitsFiguresAndScreen(placedFigures)) {
      copy(figure = newFigure)
    }
    else this
  }
}

object GameState {
  type FigureGenerator = () => Figure
}
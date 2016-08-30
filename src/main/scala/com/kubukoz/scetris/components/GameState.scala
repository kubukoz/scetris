package com.kubukoz.scetris.components

import com.kubukoz.scetris.components.GameState._
import com.kubukoz.scetris.domain.{Direction, Position, Rotation}
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen

import scala.annotation.tailrec
import scala.swing.Color

final case class GameState(figure: Figure, placedFigures: Map[Position, Color])(implicit figureGenerator: () => Figure, screen: Screen)
  extends CanDraw {

  def modifiedWith(event: GameEvent): GameState = event match {
    case MoveEvent(Direction.Down) => step
    case MoveEvent(direction) => moveFigure(direction)
    case RotateEvent(direction) => rotateFigure(direction)
  }

  override def draw(implicit screen: Screen): Drawable = GameStateDrawable(figure, placedFigures)

  protected def step: GameState = {
    if (figure.canGoDown(placedFigures))
      moveFigure(Direction.Down)
    else {
      val tempFigure = figureGenerator()
      val startingPosition = Position((screen.width - tempFigure.width) / 2, 0)

      val newFigure = tempFigure.copy(leftTop = startingPosition)

      replaceFigureIfPossible(newFigure).copy(
        placedFigures = withoutCompleteRows(placedFigures ++ figure.toMap)
      )
    }
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

  /**
    * Removes blocks that form a complete line on the screen,
    * and moves the blocks above by the amount of blocks
    * equal to the amount of lines removed.
    **/
  @tailrec
  def withoutCompleteRows(currentBlocks: Map[Position, Color])(implicit screen: Screen): Map[Position, Color] = {
    val rowsWithPositions = currentBlocks.keySet.groupBy(_.y)

    val affectedRows = rowsWithPositions.collect {
      case (y, matches) if matches.size == screen.width => y
    }.toList

    affectedRows match {
      case Nil => currentBlocks
      case _ =>
        val remainingFigures = currentBlocks.filterKeys { position => !affectedRows.contains(position.y) }

        val newFigures = remainingFigures.map {
          case (position, color) if position.y < affectedRows.max =>
            (position.copy(y = position.y + affectedRows.length), color)
          case (position, color) => (position, color)
        }

        withoutCompleteRows(newFigures)
    }
  }
}
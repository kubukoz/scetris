package com.kubukoz.scetris.components

import cats.effect.IO
import cats.implicits._
import com.kubukoz.scetris.components.GameState._
import com.kubukoz.scetris.domain.{Direction, Position, Rotation}
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen

import scala.annotation.tailrec
import scala.swing.Color

final case class GameState(
  figure: Figure,
  placedBlocks: Map[Position, Color],
  figureGenerator: IO[Figure],
  screen: Screen
) extends CanDraw {

  def modifiedWith(command: GameCommand): IO[GameState] = command match {
    case MoveCommand(Direction.Down) => step
    case MoveCommand(direction)      => moveFigure(direction).pure[IO]
    case RotateCommand(direction)    => rotateFigure(direction).pure[IO]
    case DropFigureCommand           => dropFigure
  }

  @tailrec
  private def dropFigure: IO[GameState] =
    if (figure.canGoDown(placedBlocks, screen)) {
      copy(figure = figure.moved(Direction.Down)).dropFigure
    } else step

  override def draw(env: DrawingEnv): Drawable = GameStateDrawable(figure, placedBlocks, env)

  private def step: IO[GameState] = {
    if (figure.canGoDown(placedBlocks, screen))
      moveFigure(Direction.Down).pure[IO]
    else
      figureGenerator.map { tempFigure =>
        val startingPosition = Position((screen.width - tempFigure.width) / 2, 0)

        val newFigure = tempFigure.copy(leftTop = startingPosition)

        replaceFigureIfPossible(newFigure).copy(
          placedBlocks = blocksWithoutCompleteRows(placedBlocks ++ figure.toMap, screen)
        )
      }
  }

  private def moveFigure(direction: Direction): GameState =
    replaceFigureIfPossible(figure.moved(direction))

  private def rotateFigure(rotation: Rotation) =
    replaceFigureIfPossible(figure.rotated(rotation))

  private def replaceFigureIfPossible(newFigure: Figure): GameState = {
    if (newFigure.fitsBlocksAndScreen(placedBlocks, screen)) {
      copy(figure = newFigure)
    } else this
  }
}

object GameState {
  type FigureGenerator = IO[Figure]

  /**
    * Removes blocks that form a complete line on the screen,
    * and moves the blocks above by the amount of blocks
    * equal to the amount of lines removed.
    **/
  @tailrec
  def blocksWithoutCompleteRows(currentBlocks: Map[Position, Color], screen: Screen): Map[Position, Color] = {
    val rowsWithPositions = currentBlocks.keySet.groupBy(_.y)

    val affectedRows = rowsWithPositions.collect {
      case (y, matches) if matches.size == screen.width => y
    }.toList

    affectedRows match {
      case Nil => currentBlocks
      case _ =>
        val remainingBlocks = currentBlocks.filterKeys { position =>
          !affectedRows.contains(position.y)
        }

        val newBlocks = remainingBlocks.map {
          case (position, color) if position.y < affectedRows.max =>
            (position.copy(y = position.y + affectedRows.length), color)
          case others => others
        }

        blocksWithoutCompleteRows(newBlocks, screen)
    }
  }
}

package com.kubukoz.scetris.components

import cats.effect.IO
import cats.implicits._
import com.kubukoz.scetris.components.GameState._
import com.kubukoz.scetris.domain.{Direction, Move, Position, Rotation}
import com.kubukoz.scetris.drawable._
import com.kubukoz.scetris.meta.Config.Screen

import scala.annotation.tailrec
import scala.swing.Color

final case class GameState(
  figure: PositionedFigure,
  placedBlocks: Map[Position, Color],
  figureGenerator: IO[Figure],
  screen: Screen
) extends CanDraw {

  private val downMove: Move = Move.fromDirection(Direction.Down)

  def modifiedWith(command: GameCommand): IO[GameState] = command match {
    case MoveCommand(Direction.Down) => step
    case MoveCommand(direction)      => moveFigure(Move.fromDirection(direction)).pure[IO]
    case RotateCommand(direction)    => rotateFigure(direction).pure[IO]
    case DropFigureCommand           => dropFigure
  }

  @tailrec
  private def dropFigure: IO[GameState] =
    if (figure.canGoDown(placedBlocks, screen)) {
      copy(figure = figure.moved(downMove)).dropFigure
    } else step

  override def draw(env: DrawingEnv): Drawable = GameStateDrawable(figure, placedBlocks, env)

  private def step: IO[GameState] = {
    if (figure.canGoDown(placedBlocks, screen))
      moveFigure(downMove).pure[IO]
    else
      figureGenerator.map { tempFigure =>
        val positionedFigure = PositionedFigure.atInitialPosition(tempFigure, screen)

        replaceFigureIfPossible(positionedFigure).copy(
          placedBlocks = blocksWithoutCompleteRows(placedBlocks ++ figure.positionMap, screen)
        )
      }
  }

  private def moveFigure(move: Move): GameState =
    replaceFigureIfPossible(figure.moved(move))

  private def rotateFigure(rotation: Rotation) =
    replaceFigureIfPossible(figure.rotated(rotation))

  private def replaceFigureIfPossible(newFigure: PositionedFigure): GameState = {
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

package com.kubukoz.scetris.components

import java.awt.{Color, Dimension}

import com.kubukoz.scetris.domain._
import com.kubukoz.scetris.meta.Config.Screen._
import com.kubukoz.scetris.meta.Config._

import scala.swing.event.KeyPressed
import scala.swing.{Component, _}
import scala.util.Random

class GameCanvas extends Component
  with CanvasBase with CanDrawLines {

  def newRandomFigure(): Figure = {
    val tempFigure = Figure.Types(Random.nextInt(Figure.Types.length))
      .copy(leftTop = Offset.origin)

    val startingOffset = Offset((Screen.width - tempFigure.width) / 2, 0)

    tempFigure.copy(leftTop = startingOffset)
  }

  var figure: Figure = newRandomFigure()
  var placedFigures: List[Figure] = Nil

  reactions += {
    case KeyPressed(_, DirectionKey(Direction.Down), _, _) =>
      step()
    case KeyPressed(_, DirectionKey(direction), _, _) =>
      moveFigure(direction)
      repaint()
    case KeyPressed(_, RotationKey(rotation), _, _) =>
      rotateFigure(rotation)
  }

  def rotateFigure(rotation: Rotation): Unit =
    replaceFigureIfPossible(figure.rotated(rotation))

  def step(): Unit = {
    if (!figure.canGoDown(placedFigures)) {
      placedFigures ::= figure
      figure = newRandomFigure()
    } else {
      moveFigure(Direction.Down)
    }
    repaint()
  }

  def moveFigure(direction: Direction): Unit = {
    replaceFigureIfPossible(figure.copy(
      leftTop = figure.leftTop.copy(
        figure.leftTop.x + direction.x,
        figure.leftTop.y + direction.y
      )
    ))
  }

  def replaceFigureIfPossible(newFigure: Figure): Unit = {
    if (newFigure.fitsFiguresAndScreen(placedFigures)) {
      figure = newFigure
      repaint()
    }
  }

  override protected def paintComponent(g: Graphics2D): Unit = {
    drawGrid(g)

    g.setColor(Color.GREEN)
    figure.draw(g)
    placedFigures.foreach(_.draw(g))
  }

  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(Color.LIGHT_GRAY)
    drawVerticalLines(g)
    drawHorizontalLines(g)
  }
}

object GameCanvas {
  def calculateSize = new Dimension(
    gridSize * width + gridBorder * (width - 1),
    gridSize * height + gridBorder * (height - 1)
  )
}
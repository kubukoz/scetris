package com.kubukoz.scetris.components

import java.awt.{Color, Dimension}

import com.kubukoz.scetris.domain._
import com.kubukoz.scetris.meta.Config._

import scala.swing.event.{Key, KeyPressed}
import scala.swing.{Component, _}

class GameCanvas extends Component
  with CanvasBase with CanDrawLines {

  var figure: Figure = new ZFigure(Offset(0, 0))

  reactions += {
    case KeyPressed(_, DirectionKey(direction), _, _) =>
      moveFigure(direction)
      repaint()
    case KeyPressed(_, Key.E, _, _) =>
      figure = figure.rotated(RightRotation)
      repaint()
    case KeyPressed(_, Key.Q, _, _) =>
      figure = figure.rotated(LeftRotation)
      repaint()
  }

  def step(): Unit = {
    //    moveFigure(Direction.Down)
    repaint()
  }

  def moveFigure(direction: Direction): Unit = {
    figure = figure.copy(
      leftTop = figure.leftTop.copy(
        figure.leftTop.x + direction.x,
        figure.leftTop.y + direction.y
      )
    )
  }

  override protected def paintComponent(g: Graphics2D): Unit = {
    drawGrid(g)

    g.setColor(Color.GREEN)
    figure.draw(g)
  }

  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(Color.LIGHT_GRAY)
    drawVerticalLines(g)
    drawHorizontalLines(g)
  }
}

object GameCanvas {
  def calculateSize = new Dimension(
    gridSize * tilesWidth + gridBorder * (tilesWidth - 1),
    gridSize * tilesHeight + gridBorder * (tilesHeight - 1)
  )
}

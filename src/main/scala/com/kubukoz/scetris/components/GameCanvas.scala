package com.kubukoz.scetris.components

import java.awt.{Color, Dimension}

import com.kubukoz.scetris.domain.{Direction, DirectionKey, Offset}
import com.kubukoz.scetris.meta.Config._

import scala.swing.event.{Key, KeyPressed}
import scala.swing.{Component, _}

class GameCanvas extends Component
  with CanDrawLines {
  preferredSize = GameCanvas.calculateSize

  var figure: Figure = new ZFigure(Offset(0, 0))

  focusable = true
  requestFocus()

  listenTo(keys)

  reactions += {
    case KeyPressed(_, DirectionKey(direction), _, _) =>
      moveFigure(direction)
      repaint()
    case KeyPressed(_, Key.R, _, _) =>
      figure = figure.rotatedRight
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
  }

  private def drawGrid(g: Graphics2D): Unit = {
    g.setColor(Color.LIGHT_GRAY)
    drawVerticalLines(g)
    drawHorizontalLines(g)

    g.setColor(Color.GREEN)
    figure.draw(g)
  }
}

object GameCanvas {
  def calculateSize = new Dimension(
    gridSize * tilesWidth + gridBorder * (tilesWidth - 1),
    gridSize * tilesHeight + gridBorder * (tilesHeight - 1)
  )
}
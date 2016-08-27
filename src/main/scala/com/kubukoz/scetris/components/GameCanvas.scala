package com.kubukoz.scetris.components

import java.awt.{Dimension, Graphics2D}

import com.kubukoz.scetris.drawable.Drawable
import com.kubukoz.scetris.meta.Config.Screen._
import com.kubukoz.scetris.meta.Config._

import scala.swing.Component

object GameCanvas extends Component {
  var drawEvent: Option[Drawable] = None

  focusable = true
  requestFocus()
  listenTo(keys)
  preferredSize = calculateSize

  def drawState(state: GameState): Unit = {
    drawEvent = Some(state.draw)
    repaint()
  }

  override protected def paintComponent(g: Graphics2D): Unit = {
    println(drawEvent)
    drawEvent.foreach(_.execute(g))
  }

  def calculateSize = new Dimension(
    gridSize * width + gridBorder * (width - 1),
    gridSize * height + gridBorder * (height - 1)
  )
}
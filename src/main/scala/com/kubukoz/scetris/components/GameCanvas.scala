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

  def drawState(state: GameState)(implicit screen: Screen): Unit = {
    drawEvent = Some(state.draw)
    repaint()
  }

  override protected def paintComponent(g: Graphics2D): Unit = {
    drawEvent.foreach(_.execute(g))
  }

  def calculateSize(screen: Screen) = new Dimension(
    gridSize * screen.width + gridBorder * (screen.width - 1),
    gridSize * screen.height + gridBorder * (screen.height - 1)
  )
}
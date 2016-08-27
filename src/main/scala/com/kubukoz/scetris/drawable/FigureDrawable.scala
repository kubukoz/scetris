package com.kubukoz.scetris.drawable

import java.awt.Color

import com.kubukoz.scetris.components.Block

import scala.swing._

case class FigureDrawable(blocks: Set[Block], color: Color) extends Drawable {
  override def execute(g: Graphics2D): Unit = {
    g.setColor(color)
    blocks.foreach(_.draw.execute(g))
  }
}

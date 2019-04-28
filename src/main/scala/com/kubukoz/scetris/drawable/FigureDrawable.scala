package com.kubukoz.scetris.drawable

import java.awt.Color

import cats.effect.IO
import com.kubukoz.scetris.components.Block

import cats.implicits._
import scala.swing._

case class FigureDrawable(blocks: Set[Block], color: Color, env: DrawingEnv) extends Drawable {
  override def execute(g: Graphics2D): IO[Unit] = {
    val setColor   = IO(g.setColor(color))
    val drawBlocks = blocks.toList.map(_.draw(env)).traverse_(_.execute(g))

    setColor >> drawBlocks
  }
}

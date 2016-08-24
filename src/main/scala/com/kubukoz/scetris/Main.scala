package com.kubukoz.scetris

import com.kubukoz.scetris.components.GameCanvas.FigureGenerator
import com.kubukoz.scetris.components.{Figure, GameCanvas}
import com.kubukoz.scetris.domain.Offset
import com.kubukoz.scetris.meta.Config.Screen

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn
import scala.swing._
import scala.util.Random

object Main extends SimpleSwingApplication {
  implicit val newRandomFigure: FigureGenerator = () => {
    val tempFigure = Figure.Types(Random.nextInt(Figure.Types.length))
      .copy(leftTop = Offset.origin)

    val startingOffset = Offset((Screen.width - tempFigure.width) / 2, 0)

    tempFigure.copy(leftTop = startingOffset)
  }

  val canvas = new GameCanvas


  override def top: Frame = new MainFrame {
    centerOnScreen()
    resizable = false
    contents = canvas
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)

    val refreshRate = 1000

    Future {
      while (true) {
        Thread.sleep(refreshRate)
        canvas.step()
      }
    }

    StdIn.readLine("Press enter to close...\n")
    sys.exit()
  }
}
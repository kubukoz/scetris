package com.kubukoz.scetris

import com.kubukoz.scetris.components.GameCanvas

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn
import scala.swing._

object Main extends SimpleSwingApplication {
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
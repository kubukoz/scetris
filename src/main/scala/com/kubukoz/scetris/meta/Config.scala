package com.kubukoz.scetris.meta

object Config {
  val gridSize = 20
  val gridBorder = 1

  case class Screen(width: Int, height: Int)

  object Screen {
    val Default = Screen(8, 10)
  }
}
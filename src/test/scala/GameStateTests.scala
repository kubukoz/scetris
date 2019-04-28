import org.scalatest.{FlatSpec, Matchers}

class GameStateTests extends FlatSpec with Matchers {
  "withoutCompleteRows" should "remove complete rows" in {
//    val screen = Screen(4, 8)
//
//    val i = Figure.Singletons.I
//
//    val initialBlocks = Set(
//      i.copy(leftTop = Position(0, 4)),
//      i.copy(leftTop = Position(0, 5)),
//      i.copy(leftTop = Position(0, 6)),
//      i.copy(leftTop = Position(0, 7)),
//      Figure.Singletons.Z.copy(leftTop = Position(1, 2))
//    ).flatMap(_.toMap).toMap
//
//    GameState.blocksWithoutCompleteRows(initialBlocks, screen) shouldBe
//      Figure.Singletons.Z.copy(leftTop = Position(1, 6)).toMap
  }

  "modifiedWith" should "work with DropFigureCommand" in {
//    val screen = Screen(4, 8)
//
//    import Figure.Singletons._
//    val newFigureGenerator = IO.pure(I)
//
//    val initialBlocks = T.copy(leftTop = Position(1, 4)).toMap
//
//
//    val state = GameState(Z.copy(leftTop = Position(0, 0)), initialBlocks, newFigureGenerator, screen)
//
//    val modified = state.modifiedWith(DropFigureCommand).unsafeRunSync()
//
//    modified.figure shouldBe I.copy(leftTop = Position(0, 0))
//    modified.placedBlocks shouldBe initialBlocks ++ Z.copy(leftTop = Position(0, 2)).toMap
  }
}

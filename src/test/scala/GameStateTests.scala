import com.kubukoz.scetris.components.{Figure, GameState}
import com.kubukoz.scetris.domain.Position
import com.kubukoz.scetris.meta.Config.Screen
import org.scalatest.{FlatSpec, Matchers}

class GameStateTests extends FlatSpec with Matchers {
  "withoutCompleteRows" should "remove complete rows" in {
    implicit val screen = Screen(4, 8)
    val i = Figure.Singletons.I
    val initialFigures = Set(
      i.copy(leftTop = Position(0, 4)),
      i.copy(leftTop = Position(0, 5)),
      i.copy(leftTop = Position(0, 6)),
      i.copy(leftTop = Position(0, 7)),
      Figure.Singletons.Z.copy(leftTop = Position(1, 2))
    ).flatMap(_.toMap).toMap

    GameState.withoutCompleteRows(initialFigures) shouldBe
      Figure.Singletons.Z.copy(leftTop = Position(1, 6)).toMap
  }
}
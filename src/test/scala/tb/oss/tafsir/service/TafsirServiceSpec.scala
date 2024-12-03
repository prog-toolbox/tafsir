package tb.oss.tafsir.service

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import tb.oss.tafsir.service.Client.*
import cats.effect.unsafe.implicits.global

class TafsirServiceSpec extends AnyFunSuite with Matchers {

  val ayahInterpretation: AyahInterpretation = AyahInterpretation(
    Tafsir(
      `resource_id` = 1,
      `resource_name` = "التفسير الميسر",
      `language_id` = 1,
      `slug` = "التفسير الميسر",
      `translated_name` = TafsirName(`name` = "التفسير الميسر", `language_name` = "arabic"),
      `text` =
        "(الحَمْدُ للهِ رَبِّ العَالَمِينَ) الثناء على الله بصفاته التي كلُّها أوصاف كمال، وبنعمه الظاهرة والباطنة، الدينية والدنيوية، وفي ضمنه أَمْرٌ لعباده أن يحمدوه، فهو المستحق له وحده، وهو سبحانه المنشئ للخلق، القائم بأمورهم، المربي لجميع خلقه بنعمه، ولأوليائه بالإيمان والعمل الصالح."
    )
  )

  def client(getAyahInterpretationFn: => IO[AyahInterpretation] = IO(ayahInterpretation)): Client[IO] = new Client[IO] {

    override def getAyah(surahNumber: Int, ayahNumber: Int): IO[Ayah] = ???

    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): IO[AyahInterpretation] =
      getAyahInterpretationFn

  }

  test("It should return an ayah interpretation") {
    val service = TafsirService.impl[IO](client(getAyahInterpretationFn = IO(ayahInterpretation)))
    service.getAyahInterpretation(1, 1, 2).unsafeRunAsync {
      case Right(result) => result should be(ayahInterpretation)
      case Left(error)   => fail("Should not happen")
    }
  }

  test("It should return an error when the client fails while trying to retrieve an ayah interpretation") {
    val service = TafsirService.impl[IO](
      client(getAyahInterpretationFn = IO.raiseError(new Throwable("Cannot get ayah interpretation")))
    )
    service.getAyahInterpretation(1, 1, 2).unsafeRunAsync {
      case Right(result) => fail("Should not happen")
      case Left(error)   => error.getMessage should be("Cannot get ayah interpretation")
    }
  }
}

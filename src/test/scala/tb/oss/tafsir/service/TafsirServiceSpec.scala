package tb.oss.tafsir.service

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import tb.oss.tafsir.service.Client.*
import cats.effect.unsafe.implicits.global

class TafsirServiceSpec extends AnyFunSuite with Matchers {

  val tafsirList: List[Tafsir] = List(
    Tafsir(1, "Tafsir Ibn Kathir", "Arabic", "Ibn Kathir", "Tafsir Al-Quran Al-Azim"),
    Tafsir(2, "Tafsir Al-Jalalayn", "Arabic", "Al-Mahalli and Al-Suyuti", "Tafsir Al-Jalalayn")
  )

  val chapters: List[Chapter] = List(Chapter(1, "Al-Fatihah"), Chapter(2, "Al-Baqarah"), Chapter(3, "Aal-Imrane"))

  def client(
    getTafsirListFn: => IO[List[Tafsir]] = IO(tafsirList),
    getChaptersFn: => IO[List[Chapter]] = IO(chapters)
  ): Client[IO] = new Client[IO] {
    override def getTafsirList: IO[List[Tafsir]] = getTafsirListFn

    override def getChapters: IO[List[Client.Chapter]] = getChaptersFn
  }

  test("It should return the tafsir list") {
    val service = TafsirService.impl[IO](client(getTafsirListFn = IO(tafsirList)))
    service.getTafsirList.unsafeRunAsync {
      case Right(result) => result should be(tafsirList)
      case Left(error)   => fail("Should not happen")
    }
  }

  test("It should return an error when the client fails while trying to retrieve the tafsir list") {
    val service =
      TafsirService.impl[IO](client(getTafsirListFn = IO.raiseError(new Throwable("Cannot get tafsir list"))))
    service.getTafsirList.unsafeRunAsync {
      case Right(result) => fail("Should not happen")
      case Left(error)   => error.getMessage should be("Cannot get tafsir list")
    }
  }

  test("It should return all chapters") {
    val service = TafsirService.impl[IO](client(getChaptersFn = IO(chapters)))
    service.getChapters.unsafeRunAsync {
      case Right(result) => result should be(chapters)
      case Left(error)   => fail("Should not happen")
    }
  }

  test("It should return an error when the client fails while trying to retrieve all chapters") {
    val service =
      TafsirService.impl[IO](client(getChaptersFn = IO.raiseError(new Throwable("Cannot get chapters"))))
    service.getChapters.unsafeRunAsync {
      case Right(result) => fail("Should not happen")
      case Left(error)   => error.getMessage should be("Cannot get chapters")
    }
  }
}

package tb.oss.tafsir.service

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import tb.oss.tafsir.service.Client.Tafsir
import cats.effect.unsafe.implicits.global

class TafsirServiceSpec extends AnyFunSuite with Matchers {

  val tafsirList: List[Tafsir] = List(
    Tafsir(
      1,
      "Tafsir Ibn Kathir",
      "Arabic",
      "Ibn Kathir",
      "Tafsir Al-Quran Al-Azim"
    ),
    Tafsir(
      2,
      "Tafsir Al-Jalalayn",
      "Arabic",
      "Al-Mahalli and Al-Suyuti",
      "Tafsir Al-Jalalayn"
    )
  )

  test("It should return the tafsir list") {
    val client = new Client[IO] {
      override def getTafsirList: IO[List[Tafsir]] = IO(tafsirList)
    }

    val service = TafsirService.impl[IO](client)
    service.getTafsirList.unsafeRunAsync {
      case Right(result) => result should be(tafsirList)
      case Left(error)   => fail("Should not happen")
    }
  }

  test("It should return an error when the client fails") {
    val client = new Client[IO] {
      override def getTafsirList: IO[List[Tafsir]] =
        IO.raiseError(new Throwable("Cannot get tafsir list"))
    }

    val service = TafsirService.impl[IO](client)
    service.getTafsirList.unsafeRunAsync {
      case Right(result) => fail("Should not happen")
      case Left(error)   => error.getMessage should be("Cannot get tafsir list")
    }
  }
}

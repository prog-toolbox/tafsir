package tb.oss.tafsir.service

import cats.effect.Async
import cats.implicits.*
import io.circe
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.generic.auto.*
import tb.oss.tafsir.service.Client.*
import tb.oss.tafsir.utils.AsyncExtensions.*

trait Client[F[_]] {
  def getChapters: F[List[Chapter]]
  def getTafsirList: F[List[Tafsir]]
}

object Client {
  case class Tafsir(id: Int, name: String, language: String, author: String, book_name: String)

  case class Chapter(index: Int, name: String)

  def impl[F[_]: Async](): Client[F] = new Client[F] {

    override def getChapters: F[List[Chapter]] = {
      val response = basicRequest
        .get(uri"http://api.quran-tafseer.com/quran")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[List[Chapter]])
        .send(FetchBackend())
        .toAsync[F]

      response.flatMap { res =>
        res.body match {
          case Right(chapters) => chapters.pure
          case Left(error)     => error.raiseError
        }
      }
    }

    override def getTafsirList: F[List[Tafsir]] = {
      val response = basicRequest
        .get(uri"http://api.quran-tafseer.com/tafseer")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[List[Tafsir]])
        .send(FetchBackend())
        .toAsync[F]

      response.flatMap { res =>
        res.body match {
          case Right(tafsirs) => tafsirs.pure
          case Left(error)    => error.raiseError
        }
      }
    }
  }
}

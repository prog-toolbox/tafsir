package tb.oss.tafsir.service

import cats.effect.Async
import cats.implicits.*
import io.circe
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.generic.auto.*
import sttp.model.StatusCode
import tb.oss.tafsir.service.Client.*
import tb.oss.tafsir.utils.AsyncExtensions.*

trait Client[F[_]] {
  def getSurahs: F[List[Surah]]
  def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah]
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
  def getTafsirList: F[List[Tafsir]]
}

object Client {

  case class AyahNotFound(surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Ayah '$ayahNumber' for surah '$surahNumber' was not found")

  case class AyahInterpretationNotFound(tafsirId: Int, surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Tafsir '$tafsirId' for ayah '$ayahNumber' of surah '$surahNumber' was not found")

  case class UnknownError(surahNumber: Int, ayahNumber: Int, response: Option[ResponseException[String, io.circe.Error]])
      extends Throwable(s"Unknown error occurred '$ayahNumber' of surah '$surahNumber, due to : $response")

  case class Tafsir(id: Int, name: String, language: String, author: String, book_name: String)

  case class Surah(index: Int, name: String)

  case class Ayah(sura_index: Int, sura_name: String, ayah_number: Int, text: String)

  case class AyahInterpretation(tafseer_id: Int, tafseer_name: String, ayah_url: String, ayah_number: Int, text: String)

  def impl[F[_]: Async](): Client[F] = new Client[F] {

    override def getSurahs: F[List[Surah]] = {
      val response = basicRequest
        .get(uri"http://api.quran-tafseer.com/quran")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[List[Surah]])
        .send(FetchBackend())
        .toAsync[F]

      response.flatMap { res =>
        res.body match {
          case Right(surahs) => surahs.pure
          case Left(error)   => error.raiseError
        }
      }
    }

    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] = {
      val response = basicRequest
        .get(uri"http://api.quran-tafseer.com/quran/$surahNumber/$ayahNumber")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[Ayah])
        .send(FetchBackend())
        .toAsync[F]

      response.flatMap {
        case Response(body, StatusCode.Ok, _, _, _, _) =>
          body match {
            case Right(ayah) => ayah.pure
            case Left(error) => error.raiseError
          }
        case res @ Response(body, StatusCode.NotFound, _, _, _, _) => AyahNotFound(surahNumber, ayahNumber).raiseError
        case res: Response[Either[ResponseException[String, io.circe.Error], Ayah]] =>
          UnknownError(surahNumber, ayahNumber, res.body.swap.toOption).raiseError
      }
    }

    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] = {
      val response = basicRequest
        .get(uri"http://api.quran-tafseer.com/tafseer/$tafsirId/$surahNumber/$ayahNumber")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[AyahInterpretation])
        .send(FetchBackend())
        .toAsync[F]

      response.flatMap {
        case Response(body, StatusCode.Ok, _, _, _, _) =>
          body match {
            case Right(interpretation) => interpretation.pure
            case Left(error)           => error.raiseError
          }
        case res @ Response(body, StatusCode.NotFound, _, _, _, _) =>
          AyahInterpretationNotFound(tafsirId, surahNumber, ayahNumber).raiseError
        case res: Response[Either[ResponseException[String, io.circe.Error], AyahInterpretation]] =>
          UnknownError(surahNumber, ayahNumber, res.body.swap.toOption).raiseError
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

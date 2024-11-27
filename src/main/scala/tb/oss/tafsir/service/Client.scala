package tb.oss.tafsir.service

import cats.effect.Async
import cats.implicits.*
import io.circe
import io.circe.{Decoder, HCursor}
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.generic.auto.*
import sttp.model.StatusCode
import tb.oss.tafsir.service.Client.*
import tb.oss.tafsir.utils.AsyncExtensions.*
import scala.scalajs.js.Thenable.Implicits.*
import scala.concurrent.ExecutionContext.Implicits.global

trait Client[F[_]] {
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
}

object Client {

  case class AyahInterpretationNotFound(tafsirId: Int, surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Tafsir '$tafsirId' for ayah '$ayahNumber' of surah '$surahNumber' was not found")

  case class UnknownError(surahNumber: Int, ayahNumber: Int, response: Option[ResponseException[String, io.circe.Error]])
      extends Throwable(s"Unknown error occurred '$ayahNumber' of surah '$surahNumber, due to : $response")

  case class AyahInterpretation(tafsir: Tafsir)

  case class Tafsir(
    `resource_id`: Int,
    `resource_name`: String,
    `language_id`: Int,
    `slug`: String,
    `translated_name`: TafsirName,
    `text`: String
  )

  case class TafsirName(`name`: String, `language_name`: String)

  def impl[F[_]: Async](): Client[F] = new Client[F] {
    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] = {
      val response = basicRequest
        .get(uri"https://api.quran.com/api/v4/tafsirs/$tafsirId/by_ayah/$surahNumber:$ayahNumber")
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
  }
}

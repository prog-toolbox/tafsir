package tb.oss.tafsir.service

import cats.effect.Async
import io.circe
import io.circe.{Decoder, HCursor}
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.generic.auto.*
import tb.oss.tafsir.service.Client.*
import tb.oss.tafsir.utils.AsyncExtensions.*
import tb.oss.tafsir.utils.ResponseExtensions.*
import scala.scalajs.js.Thenable.Implicits.*
import scala.concurrent.ExecutionContext.Implicits.global

trait Client[F[_]] {
  def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah]
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
  def getSurahs: F[Surahs]
}

object Client {

  case class AyahNotFound(surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Ayah '$ayahNumber' of surah '$surahNumber' was not found")

  case class AyahInterpretationNotFound(tafsirId: Int, surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Tafsir '$tafsirId' for ayah '$ayahNumber' of surah '$surahNumber' was not found")

  case class UnknownError(surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Unknown error occurred '$ayahNumber' of surah '$surahNumber")

  case class UnknownResponse() extends Throwable(s"Unknown response when trying to retrieve surahs.")

  case class AyahInterpretation(tafsir: Tafsir)

  case class Verse(`hizb_number`: Int, `page_number`: Int, `juz_number`: Int, `text_uthmani`: String)
  case class Ayah(verse: Verse)
  case class Surahs(`chapters`: List[Chapter])
  case class Chapter(`id`: Int, `name_arabic`: String, `verses_count`: Int, `revelation_place`: String)

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

    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] =
      basicRequest
        .get(uri"https://api.quran.com/api/v4/verses/by_key/$surahNumber:$ayahNumber?fields=text_uthmani")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[Ayah])
        .send(FetchBackend())
        .toAsync[F]
        .handleResponse(AyahNotFound(surahNumber, ayahNumber), UnknownError(surahNumber, ayahNumber))

    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
      basicRequest
        .get(uri"https://api.quran.com/api/v4/tafsirs/$tafsirId/by_ayah/$surahNumber:$ayahNumber")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[AyahInterpretation])
        .send(FetchBackend())
        .toAsync[F]
        .handleResponse(
          AyahInterpretationNotFound(tafsirId, surahNumber, ayahNumber),
          UnknownError(surahNumber, ayahNumber)
        )

    override def getSurahs: F[Surahs] =
      basicRequest
        .get(uri"https://api.quran.com/api/v4/chapters")
        .headers(Map("Accept" -> "application/json"))
        .response(asJson[Surahs])
        .send(FetchBackend())
        .toAsync[F]
        .handleResponse(UnknownResponse())
  }
}

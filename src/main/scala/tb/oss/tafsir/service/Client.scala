package tb.oss.tafsir.service

import cats.effect.Async
import cats.implicits.*
import io.circe
import io.circe.{Decoder, HCursor}
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.generic.auto.*
import io.circe.parser.decode
import org.scalajs.dom.ext.Ajax
import sttp.model.StatusCode
import tb.oss.tafsir.service.Client.*
import tb.oss.tafsir.utils.AsyncExtensions.*
//import org.scalajs.dom.fetch as domFetch

import scala.concurrent.Future
import scala.scalajs.js.Thenable.Implicits.*
import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.concurrent.ExecutionContext.Implicits.global

trait Client[F[_]] {
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
//  def getSurahs: F[List[Surah]]
//  def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah]
//  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
//  def getTafsirList: F[List[Tafsir]]
}

object Client {

  // Define a CORS proxy URL
  val corsProxy = "https://cors-proxy.htmldriven.com/?url="

  // Function to create a proxied URL
  def proxyUrl(url: String): String = s"$corsProxy$url"

  case class AyahNotFound(surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Ayah '$ayahNumber' for surah '$surahNumber' was not found")

  case class AyahInterpretationNotFound(tafsirId: Int, surahNumber: Int, ayahNumber: Int)
      extends Throwable(s"Tafsir '$tafsirId' for ayah '$ayahNumber' of surah '$surahNumber' was not found")

  case class UnknownError(surahNumber: Int, ayahNumber: Int, response: Option[ResponseException[String, io.circe.Error]])
      extends Throwable(s"Unknown error occurred '$ayahNumber' of surah '$surahNumber, due to : $response")

//  case class Tafsir(id: Int, name: String, language: String, author: String, book_name: String)

  case class Surah(index: Int, name: String)

  case class Ayah(sura_index: Int, sura_name: String, ayah_number: Int, text: String)

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

  case class SurahsResponse(chapters: List[Surah])

  // Add an implicit decoder for SurahsResponse
  implicit val surahsResponseDecoder: Decoder[SurahsResponse] = (c: HCursor) =>
    for {
      chapters <- c.downField("chapters").as[List[Surah]]
    } yield SurahsResponse(chapters)

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

//  def impl[F[_]: Async](): Client[F] = new Client[F] {
//
//    private def fetchAndDecode[A: Decoder](url: String): F[A] =
//      Async[F]
//        .fromFuture(
//          Async[F].delay(
//            domFetch(corsProxy + encodeURIComponent(url))
//              .flatMap(response =>
//                if (response.ok) response.text()
//                else Future.failed(new Exception(s"HTTP error! status: ${response.status}"))
//              )
////              .toFuture
//          )
//        )
//        .flatMap(text =>
////          Async[F].delay( //println(s"Raw API response: $text")) *> // Log the raw response
//            decode[A](text) match {
//              case Right(decoded) => decoded.pure[F]
//              case Left(error) => error.raiseError
////                Async[F].raiseError(new Exception(s"Decoding error: ${error.getMessage}\nRaw response: $text"))
//            }
//        )
//
//    override def getSurahs: F[List[Surah]] =
//      fetchAndDecode[List[Surah]]("http://api.quran-tafseer.com/quran")
//
//    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] =
//      fetchAndDecode[Ayah](s"http://api.quran-tafseer.com/quran/$surahNumber/$ayahNumber")
//
//    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
//      fetchAndDecode[AyahInterpretation](s"http://api.quran-tafseer.com/tafseer/$tafsirId/$surahNumber/$ayahNumber")
//
//    override def getTafsirList: F[List[Tafsir]] =
//      fetchAndDecode[List[Tafsir]]("http://api.quran-tafseer.com/tafseer")
//
////
////    override def getSurahs: F[List[Surah]] =
////      Async[F]
////        .fromFuture(Async[F].delay(Ajax.get(proxyUrl("https://api.quran-tafseer.com/quran"))))
////        .flatMap(xhr =>
////          decode[List[Surah]](xhr.responseText) match {
////            case Right(surahs) => surahs.pure
////            case Left(thr)     => thr.raiseError
////          }
////        )
////
////    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] =
////      Async[F]
////        .fromFuture(
////          Async[F].delay(Ajax.get(s"https://api.quran-tafseer.com/quran/$surahNumber/$ayahNumber"))
////        )
////        .flatMap(xhr =>
////          decode[Ayah](xhr.responseText) match {
////            case Right(ayah) => ayah.pure
////            case Left(thr)   => thr.raiseError
////          }
////        )
////
////    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
////      Async[F]
////        .fromFuture(
////          Async[F].delay(
////            Ajax.get(proxyUrl(s"http://api.quran-tafseer.com/tafseer/$tafsirId/$surahNumber/$ayahNumber"))
////          )
////        )
////        .flatMap(xhr =>
////          decode[AyahInterpretation](xhr.responseText) match {
////            case Right(interpretation) => interpretation.pure
////            case Left(thr)             => thr.raiseError
////          }
////        )
////
////    override def getTafsirList: F[List[Tafsir]] =
////      Async[F]
////        .fromFuture(Async[F].delay(Ajax.get("https://api.quran-tafseer.com/tafseer")))
////        .flatMap(xhr =>
////          decode[List[Tafsir]](xhr.responseText) match {
////            case Right(tafsirs) => tafsirs.pure
////            case Left(thr)      => thr.raiseError
////          }
////        )
//
//
////    override def getSurahs: F[List[Surah]] = {
////      val response = basicRequest
////        .get(uri"http://api.quran-tafseer.com/quran")
////        .headers(Map("Accept" -> "application/json"))
////        .response(asJson[List[Surah]])
////        .send(FetchBackend())
////        .toAsync[F]
////
////      response.flatMap { res =>
////        res.body match {
////          case Right(surahs) => surahs.pure
////          case Left(error)   => error.raiseError
////        }
////      }
////    }
////
////    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] = {
////      val response = basicRequest
////        .get(uri"http://api.quran-tafseer.com/quran/$surahNumber/$ayahNumber")
////        .headers(Map("Accept" -> "application/json"))
////        .response(asJson[Ayah])
////        .send(FetchBackend())
////        .toAsync[F]
//
////    override def getSurahs: F[List[Surah]] = {
////      val response = basicRequest
////        .get(uri"http://api.quran-tafseer.com/quran")
////        .headers(Map("Accept" -> "application/json"))
////        .response(asJson[List[Surah]])
////        .send(FetchBackend())
////        .toAsync[F]
////
////      response.flatMap { res =>
////        res.body match {
////          case Right(surahs) => surahs.pure
////          case Left(error)   => error.raiseError
////        }
////      }
////    }
////
////    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] = {
////      val response = basicRequest
////        .get(uri"http://api.quran-tafseer.com/quran/$surahNumber/$ayahNumber")
////        .headers(Map("Accept" -> "application/json"))
////        .response(asJson[Ayah])
////        .send(FetchBackend())
////        .toAsync[F]
////
////      response.flatMap {
////        case Response(body, StatusCode.Ok, _, _, _, _) =>
////          body match {
////            case Right(ayah) => ayah.pure
////            case Left(error) => error.raiseError
////          }
////        case res @ Response(body, StatusCode.NotFound, _, _, _, _) => AyahNotFound(surahNumber, ayahNumber).raiseError
////        case res: Response[Either[ResponseException[String, io.circe.Error], Ayah]] =>
////          UnknownError(surahNumber, ayahNumber, res.body.swap.toOption).raiseError
////      }
////    }
////
////    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] = {
////      val response = basicRequest
////        .get(uri"http://api.quran-tafseer.com/tafseer/$tafsirId/$surahNumber/$ayahNumber")
////        .headers(Map("Accept" -> "application/json"))
////        .response(asJson[AyahInterpretation])
////        .send(FetchBackend())
////        .toAsync[F]
////
////      response.flatMap {
////        case Response(body, StatusCode.Ok, _, _, _, _) =>
////          body match {
////            case Right(interpretation) => interpretation.pure
////            case Left(error)           => error.raiseError
////          }
////        case res @ Response(body, StatusCode.NotFound, _, _, _, _) =>
////          AyahInterpretationNotFound(tafsirId, surahNumber, ayahNumber).raiseError
////        case res: Response[Either[ResponseException[String, io.circe.Error], AyahInterpretation]] =>
////          UnknownError(surahNumber, ayahNumber, res.body.swap.toOption).raiseError
////      }
////    }
////
////    override def getTafsirList: F[List[Tafsir]] = {
////      val response = basicRequest
////        .get(uri"http://api.quran-tafseer.com/tafseer")
////        .headers(Map("Accept" -> "application/json"))
////        .response(asJson[List[Tafsir]])
////        .send(FetchBackend())
////        .toAsync[F]
////
////      response.flatMap { res =>
////        res.body match {
////          case Right(tafsirs) => tafsirs.pure
////          case Left(error)    => error.raiseError
////        }
////      }
////    }
//  }
}

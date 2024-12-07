package tb.oss.tafsir.service

import cats.*
import cats.implicits.*
import cats.effect.Async
import tb.oss.tafsir.service.Client.*

trait TafsirService[F[_]] {
  def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah]
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
  def getSurah(surahNumber: Int): F[Surah]
}

object TafsirService {

  def impl[F[_]: Async](client: Client[F]): TafsirService[F] = new TafsirService[F] {

    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] =
      client.getAyah(surahNumber, ayahNumber)

    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
      client.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)

    override def getSurah(surahNumber: Int): F[Surah] =
      client.getSurahs
        .map(_.`chapters`.filter(_.id == surahNumber).head)
        .map(Surah.fromChapter)
  }
}

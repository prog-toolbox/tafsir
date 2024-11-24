package tb.oss.tafsir.service

import cats.*
import cats.implicits.*
import cats.effect.Async
import tb.oss.tafsir.service.Client.*

trait TafsirService[F[_]] {
  def getSurahs: F[List[Surah]]
  def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah]
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
  def getTafsirList: F[List[Tafsir]]

  def interpret(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[DetailedAyah]
}

object TafsirService {

  def impl[F[_]: Async](client: Client[F]): TafsirService[F] = new TafsirService[F] {
    override def getSurahs: F[List[Surah]] = client.getSurahs
    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] = client.getAyah(surahNumber, ayahNumber)
    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
      client.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
    override def getTafsirList: F[List[Tafsir]] = client.getTafsirList

    override def interpret(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[DetailedAyah] =
      for {
        surah <- getSurahs.map(_.filter(_.index == surahNumber)).map(_.head)
        ayah <- getAyah(surahNumber, ayahNumber)
        tafsir <- getTafsirList.map(_.filter(_.id == tafsirId)).map(_.head)
        interpretation <- getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
      } yield DetailedAyah(surahNumber, surah.name, ayahNumber, ayah.text, tafsir.name, interpretation.text)
  }
}

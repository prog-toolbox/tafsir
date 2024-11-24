package tb.oss.tafsir.service

import cats.effect.Async
import tb.oss.tafsir.service.Client.*

trait TafsirService[F[_]] {
  def getSurahs: F[List[Surah]]
  def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah]
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
  def getTafsirList: F[List[Tafsir]]
}

object TafsirService {

  def impl[F[_]: Async](client: Client[F]): TafsirService[F] = new TafsirService[F] {
    override def getSurahs: F[List[Surah]] = client.getSurahs
    override def getAyah(surahNumber: Int, ayahNumber: Int): F[Ayah] = client.getAyah(surahNumber, ayahNumber)
    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
      client.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
    override def getTafsirList: F[List[Tafsir]] = client.getTafsirList
  }
}

package tb.oss.tafsir.service

import cats.*
import cats.effect.Async
import tb.oss.tafsir.service.Client.*

trait TafsirService[F[_]] {
  def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation]
}

object TafsirService {

  def impl[F[_]: Async](client: Client[F]): TafsirService[F] = new TafsirService[F] {
    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): F[AyahInterpretation] =
      client.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
  }
}

package tb.oss.tafsir.service

import cats.effect.Async
import tb.oss.tafsir.service.Client.{Chapter, Tafsir}

trait TafsirService[F[_]] {
  def getChapters: F[List[Chapter]]
  def getTafsirList: F[List[Tafsir]]
}

object TafsirService {

  def impl[F[_]: Async](client: Client[F]): TafsirService[F] =
    new TafsirService[F] {
      override def getChapters: F[List[Chapter]] = client.getChapters
      override def getTafsirList: F[List[Tafsir]] = client.getTafsirList
    }
}

package tb.oss.tafsir.utils

import cats.effect.Async

import scala.concurrent.Future

object AsyncExtensions {

  extension[A](future: Future[A]) {
    def toAsync[F[_]: Async]: F[A] =
      Async[F].fromFuture(Async[F].delay(future))
  }
}

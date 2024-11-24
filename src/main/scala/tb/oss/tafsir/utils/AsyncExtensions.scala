package tb.oss.tafsir.utils

import cats.effect.Async

import scala.concurrent.{ExecutionContext, Future}

object AsyncExtensions {

  implicit class FutureOps[A](val future: Future[A]) extends AnyVal {
    def toAsync[F[_]: Async]: F[A] =
      Async[F].fromFuture(Async[F].delay(future))
  }
}

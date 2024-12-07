package tb.oss.tafsir.utils

import cats.effect.Async
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.syntax.either.*
import cats.MonadError
import sttp.client3.{Response, ResponseException}
import sttp.model.StatusCode

object ResponseExtensions {

  extension[F[_]: Async, E, A, B <: Throwable, C <: Throwable](logic: F[Response[Either[ResponseException[E, io.circe.Error], A]]]) {
    def handleResponse(notFound: B, unknownError: C): F[A] = {
      logic.flatMap {
        case Response(body, StatusCode.Ok, _, _, _, _) =>
          body match {
            case Right(value) => Async[F].pure(value)
            case Left(error)  => Async[F].raiseError(error)
          }
        case Response(_, StatusCode.NotFound, _, _, _, _) =>
          Async[F].raiseError(notFound)
        case Response(body, _, _, _, _, _) =>
          Async[F].raiseError(unknownError.initCause(body.swap.toOption.get))
      }
    }
    def handleResponse(unknownError: C): F[A] = {
      logic.flatMap {
        case Response(body, StatusCode.Ok, _, _, _, _) =>
          body match {
            case Right(value) => Async[F].pure(value)
            case Left(error)  => Async[F].raiseError(error)
          }
        case Response(body, _, _, _, _, _) =>
          Async[F].raiseError(unknownError.initCause(body.swap.toOption.get))
      }
    }
  }
}

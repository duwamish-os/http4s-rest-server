package server

import cats.effect.IO
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends StreamApp[IO] with Http4sDsl[IO] {
  val service = HttpService[IO] {
    case GET -> Root / "api" / "customer" / customerID =>
      Ok(s"welcome to sellpeace.com, Mr. $customerID")
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}

package server

import cats.effect.IO
import fs2.StreamApp

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import schema.{ChatRequest, ChatResponse}

import scala.concurrent.ExecutionContext.Implicits.global

object RestServer extends StreamApp[IO] with Http4sDsl[IO] {

  implicit val decoder = jsonOf[IO, ChatRequest]
  implicit val encoder = jsonEncoderOf[IO, ChatResponse]

  val service: HttpService[IO] = HttpService[IO] {

    case GET -> Root / "api" / "chat" / "history" / customerID =>
      Ok(Json.obj("body" -> Json.fromString(s"welcome to sellpeace.com, Mr. $customerID")))

    case request@POST -> Root / "api" / "chat" =>
      for {
        chatRequest <- request.as[ChatRequest]
        chatResponse <- Ok(ChatResponse(chatRequest.correlationId, "hi, how can i help you?"))
      } yield chatResponse

  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}

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
import schema.{ChatHistory, ChatRequest, ChatResponse}

import scala.concurrent.ExecutionContext.Implicits.global

object RestServer extends StreamApp[IO] with Http4sDsl[IO] {

  implicit val chatRequestDecoder = jsonOf[IO, ChatRequest]
  implicit val chatResponseEncoder = jsonEncoderOf[IO, ChatResponse]
  implicit val chatHistoryEncoder = jsonEncoderOf[IO, ChatHistory]

  val service: HttpService[IO] = HttpService[IO] {

    case req@GET -> Root / "api" / "chat" / "history" / customerID =>
      val correlationId = req.headers.find(_.name == "correlationId").map(_.value).getOrElse("")
      Ok(ChatHistory(correlationId, List("hi, how can i help you?", "here is coffee shop")))

    case request@POST -> Root / "api" / "chat" =>
      for {
        chatRequest <- request.as[ChatRequest]
        chatResponse <- Ok(ChatResponse(chatRequest.correlationId, "Here are near by coffee shops"))
      } yield chatResponse

  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}

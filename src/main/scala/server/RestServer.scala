package server

import cats.data.{Kleisli, OptionT}
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
import scala.concurrent.Future
import org.http4s.rho.RhoService

object RestServer extends StreamApp[IO] with Http4sDsl[IO] {

  implicit val chatRequestDecoder = jsonOf[IO, ChatRequest]
  implicit val chatResponseEncoder = jsonEncoderOf[IO, ChatResponse]
  implicit val chatHistoryEncoder = jsonEncoderOf[IO, ChatHistory]

  //type ChatService[F[_]] = Kleisli[OptionT[F, ?], MyHeaders, ChatHistory]

  val chatApi = new RhoService[IO] {
    //"retrieve chat history for a given userId" **
    GET / "chat" / pathVar[String]("userId", "userId") +? paramD[Int]("historyLimit", "number of chats") |>> { (userId: String, historyLimit: Int) => {

      val chatHistResponse = Future(ChatHistory("TODO", List("here is a coffee shop near to you.", "does that help you?")))

      Ok(ChatHistory("read from header", List("here is a coffee shop near to you.", "does that help you?")))
    }
    }
  }

  val service: HttpService[IO] = HttpService[IO] {

    case req@GET -> Root / "api" / "chat" / "history" / (userId: String) =>
      val correlationId = req.headers.find(_.name == "correlationId").map(_.value).getOrElse("")
      val chatHistResponse: IO[Response[IO]] =
        Ok(Future.successful(ChatHistory(
          correlationId,
          List("hi, how can i help you?", "here is coffee shop")))
        )
      chatHistResponse

    case request@POST -> Root / "api" / "chat" =>
      for {
        chatRequest <- request.as[ChatRequest]
        chatResponse <- Ok(Future.successful(ChatResponse(
          chatRequest.correlationId,
          "Here are near by coffee shops"))
        )
      } yield chatResponse

  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}

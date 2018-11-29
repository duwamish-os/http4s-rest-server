package server

import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import cats.effect.IO
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import schema.{ChatHistory, ChatRequest, ChatResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.http4s.rho.RhoService

object RestServer extends IOApp {

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

  val service: Kleisli[IO, Request[IO], Response[IO]] = HttpRoutes.of[IO] {

    case req@GET -> Root / "api" / "chat" / "history" / (userId: String) =>
      val correlationId = req.headers.find(_.name == "correlationId").map(_.value).getOrElse("")
      val chatHistResponse: IO[Response[IO]] =
        Ok(ChatHistory(
          correlationId,
          List("hi, how can i help you?", "here is coffee shop"))
        )
      chatHistResponse

    case request@POST -> Root / "api" / "chat" =>
      for {
        chatRequest <- request.as[ChatRequest]
        chatResponse <- Ok(ChatResponse(
          chatRequest.correlationId,
          "Here are near by coffee shops")
        )
      } yield chatResponse

  }.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}

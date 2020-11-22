package tasks.basics

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.syntax.all._
import org.http4s._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import com.github.blemale.scaffeine.{Cache, Scaffeine}
import scala.util.Random
object httpHomework {}

object HttpServer extends IOApp {

  case class GameOpt(answ: Int, maxAtmpt: Int)

  object GameOpt {

    def of(min: Int, max: Int, maxAtmpt: Int): GameOpt =
      GameOpt(Random.between(min, max), maxAtmpt)
  }

  val cache: Cache[String, GameOpt] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(1.hour)
      .maximumSize(100)
      .build[String, GameOpt]()

  private val paramsRoutes = {

    object NameMatcher extends QueryParamDecoderMatcher[String]("name")
    object MinMatcher extends QueryParamDecoderMatcher[Int]("min")
    object MaxMatcher extends QueryParamDecoderMatcher[Int]("max")
    object MaxAtmpMatcher extends QueryParamDecoderMatcher[Int]("maxAtmpt")
    object GuessMatcher extends QueryParamDecoderMatcher[Int]("guess")

    HttpRoutes.of[IO] {
      // http://localhost:9001/new?name=test2&min=0&max=10&maxAtmpt=4
      case req @ GET -> Root / "new"
          :? NameMatcher(name)
          +& MinMatcher(min)
          +& MaxMatcher(max)
          +& MaxAtmpMatcher(maxAtmpt) =>
        cache.getIfPresent(name) match {
          case Some(_) =>
            Ok(s"Game '$name' with this name already exists, please try different name!")
          case None => {
            cache.put(name, GameOpt.of(min, max, maxAtmpt))
//            Answer provided for dev purposes
            Ok(
              s"Game created successfully go to '/play, answer is: '${cache.getIfPresent(name).get.answ}"
            )
          }
        }

// http://localhost:9001/play?name=test2&guess=4
      case req @ GET -> Root / "play"
          :? NameMatcher(name)
          +& GuessMatcher(guess) => {
        cache.getIfPresent(name) match {
          case None =>
            Ok(
              "No game with such name exists, make sure name is correct or create new game at '/new'"
            )
          case Some(gameOpt) =>
            (gameOpt.answ, gameOpt.maxAtmpt) match {
              case (answ, 1) => {
                cache.invalidate(name)
                answ match {
                  case `guess` => Ok(s"won")
                  case _ =>
                    Ok(s"end")
                }

              }
              case (`guess`, _) => {
                cache.invalidate(name)
                Ok(s"won")
              }
              case (answ, _) => {
                cache.put(name, GameOpt(gameOpt.answ, gameOpt.maxAtmpt - 1))
                (answ > guess) match {
                  case true  => Ok("<")
                  case false => Ok(">")
                }
              }

            }
        }
      }
    }
  }

  private val httpApp = { paramsRoutes }.orNotFound

  override def run(args: List[String]): IO[ExitCode] = {

    BlazeServerBuilder[IO](ExecutionContext.global)
      .bindHttp(port = 9001, host = "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}

object HttpClient extends IOApp {

  private val uri = uri"http://localhost:9001"

  private def printLine(string: String = ""): IO[Unit] = IO(println(string))
//
  val max: Int = 100
  val min: Int = 0
  val maxAtmpt: Int = 5

  def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](ExecutionContext.global).resource
      .parZip(Blocker[IO])
      .use {
        case (client, blocker) =>
          for {
            _ <- printLine(string = "Executing simple GET and POST requests:")
            _ <- client.expect[String](
              (uri / "new").withQueryParams(
                Map(
                  "name"     -> "test",
                  "min"      -> min.toString,
                  "max"      -> max.toString,
                  "maxAtmpt" -> maxAtmpt.toString
                )
              )
            ) >>= printLine
            _ <- {
// Recursion should be safe (trampolined) https://typelevel.org/cats-effect/datatypes/io.html#stack-safety
              def guess(min: Int, max: Int): IO[String] = {
                val g: Int = Random.between(min, max)
                println(g)
                client
                  .expect[String](
                    (uri / "play").withQueryParams(
                      Map("name" -> "test", "guess" -> g.toString)
                    )
                  )
                  .flatMap(x => {
                    println(x)
                    x match {
                      case "end" => IO("Client lost!")
                      case "won" => IO("Client won!")
                      case "<"   => guess(g, max)
                      case ">"   => guess(min, g)
                      case _     => IO("Error in game!")
                    }
                  })
              }
              guess(min, max)
            } >>= printLine

          } yield ()
      }
      .as(ExitCode.Success)
}

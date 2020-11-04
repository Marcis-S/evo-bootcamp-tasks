package tasks.basics

import java.net.URL
import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Success}
import scala.collection.parallel.CollectionConverters._

/**
  * Application:
  * - takes a web-page URL from arguments (args array)
  * - loads the web-page body, extracts HTTP links from it
  * - for all the found links, tries to fetch a server name header if there is one
  * - prints all the encountered unique server name values in alphabetical order
  *
  * Each link processing should be done in parallel.
  * Validation of arguments is not needed.
  *
  * Try to test it on http://google.com!
  */
object AsyncHomework extends App {

  private implicit val ec: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  def linkServers(webPage: String): Unit = {

    val urls = for {
      body <- fetchPageBody(webPage)
      urls <- findLinkUrls(body)
    } yield urls

    urls.onComplete {
      case Success(urls) =>
        val servers = Future.sequence(urls.par.map(x => fetchServerName(x)).toList)
        servers.onComplete {
          case Success(value) =>
            println(
              value.distinct
                .map(_.getOrElse(""))
                .filter(_.nonEmpty)
                .sortWith(_.toLowerCase < _.toLowerCase)
            )
          case Failure(exception) => println(exception.getCause)
        }
      case Failure(exception) => println(exception.getCause)
    }

  }

  args.foreach(linkServers)
// For testing uncomment:
// linkServers("http://google.com")

  private def fetchPageBody(url: String): Future[String] = {
    println(f"Fetching $url")
    Future {
      val source = Source.fromURL(url)
      try {
        source.mkString
      } finally {
        source.close()
      }
    }
  }

  private def fetchServerName(url: String): Future[Option[String]] = {
    println(s"Fetching server name header for $url")
    Future {
      Option(new URL(url).openConnection().getHeaderField("Server"))
    }
  }

  private def findLinkUrls(html: String): Future[List[String]] =
    Future {
      val linkPattern = """href="(http[^"]+)"""".r
      linkPattern.findAllMatchIn(html).map(m => m.group(1)).toList
    }
}

package scalaguide.advanced.filters.routing

// #routing-info-access
import javax.inject.Inject
import akka.stream.FlowMaterializer
import play.api.mvc.{Result, RequestHeader, Filter}
import play.api.Logger
import play.api.routing.Router.Tags
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class LoggingFilter @Inject() (implicit val mat: FlowMaterializer) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val action = requestHeader.tags(Tags.RouteController) +
        "." + requestHeader.tags(Tags.RouteActionMethod)
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      Logger.info(s"${action} took ${requestTime}ms" +
        s" and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
// #routing-info-access
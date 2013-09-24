import conf.Config
import controllers.GraphiteClient
import play.api.{Application, GlobalSettings}
import play.libs.Akka
import play.Logger
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    val Some(services) = Config.services

    for ((serviceId, serviceName) <- services) {
      Logger.info(s"Following service $serviceName with ID $serviceId")
    }

    Akka.system.scheduler.schedule(0 seconds, 60 seconds) {
      Logger.info("refreshing Graphite")
      GraphiteClient.sendToGraphite
      Logger.info("done")
    }
  }
}

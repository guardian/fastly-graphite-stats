package controllers

import model.{ StatsForService, StatsParser }
import com.codahale.metrics.graphite.Graphite
import java.net.InetSocketAddress
import play.Logger
import conf.Config

object GraphiteClient extends FetchingStats {
  lazy val graphiteClient = (for {
    host <- Config.graphiteHost
    port <- Config.graphitePort
  } yield new Graphite(new InetSocketAddress(host, port))).getOrElse(
    throw new RuntimeException("You must provide host and port of Graphite"))

  def sendToGraphite = {
    if (management.Switches.sendToGraphiteSwitch) {
      Logger.info("sendToGraphite switch is on")
      send(fetch())
    } else {
      Logger.info("sendToGraphite switch is off")
    }
  }

  def send(fs: Map[String, StatsForService]) = {
    try {
      Logger.info("Connecting to graphite")
      graphiteClient.connect()

      if (fs.isEmpty) {
        Logger.warn("Tried to send empty stats map to Graphite")
      }

      fs foreach {
        case (serviceId, stats) => {
          val from = StatsParser.toDateTime(stats.meta.from)
          var minutes = 1
          val serviceName = ServiceFilter.services(serviceId)

          if (stats.data.isEmpty) {
            Logger.info(s"No data for $serviceName")
          }

          stats.data foreach { stat =>
            val metricSecondsSinceEpoc = from.plusMinutes(minutes).getMillis / 1000

            Logger.info(stat.toString)

            val requestMetricName = "fastly.%s.requests".format(serviceName)
            graphiteClient.send(
              requestMetricName,
              stat.requests.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_1xx".format(serviceName),
              stat.status_1xx.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_2xx".format(serviceName),
              stat.status_2xx.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_3xx".format(serviceName),
              stat.status_3xx.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_301".format(serviceName),
              stat.status_301.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_302".format(serviceName),
              stat.status_302.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_4xx".format(serviceName),
              stat.status_4xx.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.http_5xx".format(serviceName),
              stat.status_5xx.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.bandwidth".format(serviceName),
              stat.bandwidth.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.hits".format(serviceName),
              stat.hits.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.miss".format(serviceName),
              stat.miss.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.pass".format(serviceName),
              stat.pass.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.hits_time".format(serviceName),
              stat.hits_time.toString,
              metricSecondsSinceEpoc)

            graphiteClient.send(
              "fastly.%s.miss_time".format(serviceName),
              stat.miss_time.toString,
              metricSecondsSinceEpoc)

            minutes = minutes + 1
          }
        }
        case other => Logger.error(s"Unexpected case: $other")
      }
    } catch {
      case e: Throwable => Logger.error("Could not send to graphite: " + e.getMessage)
    } finally {
      Logger.info("Closing graphite client")
      graphiteClient.close()
    }
  }
}

object ServiceFilter {
  val services = Config.services.getOrElse(throw new RuntimeException("You must provide list of services to track"))

  lazy val serviceIds = services.keys

  lazy val graphiteStatNames = services.values
}
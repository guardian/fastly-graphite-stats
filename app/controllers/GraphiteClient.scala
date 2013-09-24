package controllers

import model.{StatsParser, FastlyStats}
import com.codahale.metrics.graphite.Graphite
import java.net.InetSocketAddress
import play.Logger
import conf.Config

object GraphiteClient extends FetchingStats {
  lazy val graphiteClient = (for {
    host <- Config.graphiteHost
    port <- Config.graphitePort
  } yield new Graphite(new InetSocketAddress(host, port))).getOrElse(
    throw new RuntimeException("You must provide host and port of Graphite")
  )

  def sendToGraphite = {
    if (management.Switches.sendToGraphiteSwitch) {
      val jsValue = fetch()
      val fastlyStats = model.StatsParser.parse(jsValue)
      GraphiteClient.send(fastlyStats)
    } else {
      Logger.info("sendToGraphite switch is off")
    }
  }

  def send(fs: FastlyStats) = {

    try {
      graphiteClient.connect()
      ServiceFilter.getServicesWeCareAbout(fs).map {
        case (serviceId, stats) =>

          val from = StatsParser.toDateTime(fs.meta.from)
          var minutes = 1

          stats.foreach {
            stat =>
              val metricSecondsSinceEpoc = from.plusMinutes(minutes).getMillis / 1000
              val serviceName = ServiceFilter.services(serviceId)

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
                "fastly.%s.hit_ratio".format(serviceName),
                stat.hit_ratio.toString,
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

    } catch {
      case e: Exception => Logger.error("Could not send to graphite: " + e.getMessage)
    } finally {
      graphiteClient.close()
    }
  }

  object ServiceFilter {
    val services = Config.services.getOrElse(throw new RuntimeException("You must provide list of services to track"))

    def getServicesWeCareAbout(fs: FastlyStats) = fs.data.filterKeys(services.keySet.contains)
  }
}
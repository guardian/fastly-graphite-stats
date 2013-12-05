package controllers

import org.joda.time.{DateTimeZone, DateTime}
import play.api.libs.json.Json
import play.Logger
import com.gu.fastly.api.By
import lib.Implicits._
import model.{StatsParser, StatsForService}

trait FetchingStats extends FastlyClient {

  private def now = new DateTime(DateTimeZone.UTC).toNearestMinute

  protected def fetch(from: Option[DateTime] = None, to: Option[DateTime] = None): Map[String, StatsForService] = {
    val f = from.getOrElse(now.minusMinutes(15))
    val t = to.getOrElse(now)

    Logger.info("fetching stats from %s to %s".format(f.toString(), t.toString()))

    (for {
      serviceId <- ServiceFilter.serviceIds
    } yield {
      val responseBody = client.statsForService(from = f, to = t, by = By.minute, serviceId = serviceId).get.getResponseBody
      Logger.info(s"Received response body: $responseBody")
      val json = Json.parse(responseBody)
      serviceId -> StatsParser.parse(json)}).toMap
  }
}

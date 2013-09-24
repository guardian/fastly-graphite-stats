package controllers

import org.joda.time.{DateTimeZone, DateTime}
import play.api.libs.json.{Json, JsValue}
import play.Logger
import com.gu.By
import lib.Implicits._

trait FetchingStats extends FastlyClient {

  private def now = new DateTime(DateTimeZone.UTC).toNearestMinute

  protected def fetch(from: Option[DateTime] = None, to: Option[DateTime] = None): JsValue = {
    val f = from.getOrElse(now.minusMinutes(15))
    val t = to.getOrElse(now)

    Logger.info("fetching stats from %s to %s".format(f.toString(), t.toString()))

    val json = client.stats(from = f, to = t, by = By.minute).get.getResponseBody
    Json.parse(json)
  }
}

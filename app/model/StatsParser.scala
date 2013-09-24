package model

import org.joda.time.{DateTimeZone, DateTime}
import play.api.libs.json._
import java.text.SimpleDateFormat
import java.util.Date

object StatsParser {
  def parse(jsValue: JsValue): FastlyStats = {
    import FastlyStatsFormats._
    val stats: FastlyStats = jsValue.as[FastlyStats]
    LastUpdated.lastUpdated = toDateTime(stats.meta.to)
    stats
  }

  def toDateTime(dateString: String): DateTime = {
    val fmt = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy")
    fmt.setTimeZone(DateTimeZone.UTC.toTimeZone)
    val parse: Date = fmt.parse(dateString.replace(" UTC", ""))
    new DateTime(parse)
  }
}

object LastUpdated {
  var lastUpdated: DateTime = _
}

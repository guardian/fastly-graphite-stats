package lib

import org.joda.time.{DateTimeZone, DateTime}

object Implicits {
  implicit class RichDateTime(dateTime: DateTime) {
    def toNearestMinute = dateTime.withSecondOfMinute(0).withMillisOfSecond(0)
  }

  implicit class RichString(str: String) {
    def toUTCDateTime = new DateTime(str, DateTimeZone.UTC)
  }
}

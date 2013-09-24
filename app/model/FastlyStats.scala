package model

import scala.Predef._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._


object FastlyStatsFormats {
  implicit val statsReader = (
    (__ \ 'service_id).read[String] and
      (__ \ 'requests).read[String].map(_.toLong) and
      (__ \ 'hits).read[String].map(_.toLong) and
      (__ \ 'miss).read[String].map(_.toLong) and
      (__ \ 'pass).read[String].map(_.toLong) and
      (__ \ 'miss_time).read[String].map(_.toDouble) and
      (__ \ 'hits_time).read[String].map(_.toDouble) and
      (__ \ 'hit_ratio).read[Option[String]].map {
        optionString: Option[String] =>
          optionString.getOrElse("0").toDouble
      } and
      (__ \ 'bandwidth).read[String].map(_.toLong) and
      (__ \ 'status_1xx).read[String].map(_.toLong) and
      (__ \ 'status_2xx).read[String].map(_.toLong) and
      (__ \ 'status_3xx).read[String].map(_.toLong) and
      (__ \ 'status_4xx).read[String].map(_.toLong) and
      (__ \ 'status_5xx).read[String].map(_.toLong) and
      (__ \ 'start_time).read[String].map {
        millis => new DateTime(millis.toLong)
      } and
      (__ \ 'status_301).read[String].map(_.toLong) and
      (__ \ 'status_302).read[String].map(_.toLong)
    )(Stats.apply _)
  implicit val statsWriter = Json.writes[Stats]

  implicit val meta = Json.format[Meta]
  implicit val fs = Json.format[FastlyStats]
}

case class FastlyStats(status: String, meta: Meta, msg: Option[String], data: Map[String, List[Stats]])

case class Meta(to: String, from: String, by: String, region: String)

// only 22 args allowed in a constructor/apply method in scala
// only 20 args allowed when deserializing using Play! Json
case class Stats(
                  service_id: String,
                  requests: Long,
                  hits: Long,
                  miss: Long,
                  pass: Long,
                  miss_time: Double,
                  hits_time: Double,
                  hit_ratio: Double,
                  bandwidth: Long,
                  status_1xx: Long,
                  status_2xx: Long,
                  status_3xx: Long,
                  status_4xx: Long,
                  status_5xx: Long,
                  // body_size: Option[String],
                  // header_size: Option[String],
                  // pipe: Option[String],
                  // status_503: Option[Long],
                  // uncacheable: Option[Long],
                  start_time: DateTime,
                  // status_200: Option[Long],
                  // status_204: Option[Long],
                  status_301: Long,
                  status_302: Long
                  // status_304: Option[Long],
                  // errors: Option[Long],
                  )

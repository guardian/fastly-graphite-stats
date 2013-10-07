package model

import play.api.libs.json._

object FastlyStatsFormats {
  implicit val statsReader = Json.reads[Stats]
  implicit val statsWriter = Json.writes[Stats]

  implicit val meta = Json.format[Meta]
  implicit val fs = Json.format[FastlyStats]
}

case class FastlyStats(status: String, meta: Meta, msg: Option[String], data: Map[String, List[Stats]])

case class Meta(to: String, from: String, by: String, region: String)

case class Stats(
    service_id: String,
    requests: Long,
    hits: Long,
    miss: Long,
    pass: Long,
    miss_time: Double,
    hits_time: Double,
    bandwidth: Long,
    status_1xx: Long,
    status_2xx: Long,
    status_3xx: Long,
    status_4xx: Long,
    status_5xx: Long,
    start_time: Long,
    status_301: Long,
    status_302: Long
)

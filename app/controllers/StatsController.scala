package controllers

import play.api.mvc._
import scala.Predef._
import lib.Implicits._

object StatsController extends Controller with FetchingStats {
  // ?from=2013-01-28T15:00&to=2013-01-28T16:00
  def view(from: Option[String], to: Option[String]) = Action {
    Ok(fetch(from.map(_.toUTCDateTime.toNearestMinute), to.map(_.toUTCDateTime.toNearestMinute)))
  }
}
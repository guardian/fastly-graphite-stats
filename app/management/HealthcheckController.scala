package management

import play.api.mvc._
import model.LastUpdated
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import conf.Config

object HealthcheckController extends Controller {
  
  def healthcheck = Action {
    val now = DateTime.now(DateTimeZone.UTC)
    val lastUpdated = LastUpdated.lastUpdated
    
    val durationInMins = Config.acceptableDurationInMinsWithNoStats
    if (lastUpdated.isBefore(now.minus(Duration.standardMinutes(durationInMins))))
    	Status(500).as(s"last updated more than durationInMins mins ago: $lastUpdated")
    else
    	Ok("last updated: %s".format(LastUpdated.lastUpdated))
  }
}
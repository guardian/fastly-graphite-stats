package management

import org.joda.time.{ DateTime, Duration }
import org.specs2.mutable.Specification

import conf.Config
import model.LastUpdated
import play.api.test.{ FakeApplication, FakeRequest }
import play.api.test.Helpers._

class HealthcheckControllerSpec extends Specification {

  sequential

  "healthcheck" should {
    "return Ok if last update was within the acceptable duration" in {
      running(FakeApplication()) {
        val acceptableDuration = Config.acceptableDurationInMinsWithNoStats
        val now = DateTime.now
        val lastUpdated = now.minus(Duration.standardMinutes(acceptableDuration - 1))

        LastUpdated.lastUpdated = lastUpdated

        val resp = route(FakeRequest(GET, "/management/healthcheck")).get

        status(resp) mustEqual OK
      }
    }

    "return 500 if last update was longer than acceptable duration" in {
      running(FakeApplication()) {
        val acceptableDuration = Config.acceptableDurationInMinsWithNoStats
        val now = DateTime.now
        val lastUpdated = now.minus(Duration.standardMinutes(acceptableDuration + 1))

        LastUpdated.lastUpdated = lastUpdated

        val resp = route(FakeRequest(GET, "/management/healthcheck")).get

        status(resp) mustEqual 500
      }
    }
  }

}
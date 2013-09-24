package management

import play.api.mvc._
import model.LastUpdated

object HealthcheckController extends Controller {
  def healthcheck = Action {
    Ok("last updated: %s".format(LastUpdated.lastUpdated))
  }
}

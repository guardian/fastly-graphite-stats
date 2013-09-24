package management

import play.api.mvc.{Action, Controller}

object Switches extends Controller {
  var sendToGraphiteSwitch = true

  def switch(sendToGraphite: Option[Boolean]) = Action {
    sendToGraphite.foreach(sendToGraphiteSwitch = _)
    Ok("sendToGraphiteSwitch: %s".format(sendToGraphiteSwitch))
  }
}
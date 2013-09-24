package management

import play.api.mvc._
import java.util.Date
import buildinfo.BuildInfo

object ManifestController extends Controller {
  def manifest = Action {
    val data = Map(
      "Build" -> BuildInfo.buildNumber,
      "Date" -> new Date(BuildInfo.buildTime).toString
    )

    val manifest: String = data.map({
      case (k, v) => s"$k: $v"
    }).mkString("\n")

    Ok(manifest)
  }
}

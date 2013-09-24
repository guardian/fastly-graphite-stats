package lib

import play.api.Configuration
import scala.collection.JavaConversions._

object PlayConfig {
  implicit class RichPlayConfig(config: Configuration) {
    def getStringStringMap(path: String) = for {
        configObject <- config.getObject(path)
      } yield configObject.mapValues(_.unwrapped().asInstanceOf[String])
  }
}

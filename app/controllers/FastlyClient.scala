package controllers

import com.gu.FastlyAPIClient
import conf.Config

trait FastlyClient {
  lazy val client = (for {
    apiKey <- Config.fastlyApiKey
    serviceId <- Config.fastlyServiceId
  } yield FastlyAPIClient(apiKey, serviceId, proxyServer = Config.proxy)) getOrElse {
    throw new RuntimeException("Fastly API key and Service ID must be set in Play config file")
  }
}

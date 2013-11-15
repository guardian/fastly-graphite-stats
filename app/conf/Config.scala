package conf

import play.api.Play
import lib.PlayConfig._
import com.ning.http.client.ProxyServer
import com.ning.http.client.ProxyServer.Protocol

object Config {
  private val playConfig = Play.current.configuration

  val fastlyApiKey = playConfig.getString("fastly.api_key")
  val fastlyServiceId = playConfig.getString("fastly.service_id")
  val services = playConfig.getStringStringMap("services")
  val graphiteHost = playConfig.getString("graphite.host")
  val graphitePort = playConfig.getInt("graphite.port")

  val proxyHost = playConfig.getString("proxy.host")
  val proxyPort = playConfig.getInt("proxy.port")
  val proxyHttps = playConfig.getBoolean("proxy.https").getOrElse(false)
  
  val acceptableDurationInMinsWithNoStats = playConfig.getInt("healthcheck.duration").getOrElse(5)

  val proxy = for {
    host <- proxyHost
    port <- proxyPort
  } yield new ProxyServer(if (proxyHttps) Protocol.HTTPS else Protocol.HTTP, host, port)
}

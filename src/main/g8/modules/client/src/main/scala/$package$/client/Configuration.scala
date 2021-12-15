package $package$.client

import $package$.client.Configuration.ClientConfiguration
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.{Has, Layer}
import zio.config.typesafe.TypesafeConfig

case class Configuration(client: ClientConfiguration)

object Configuration {
  val serverConfigurationDescriptor = descriptor[Configuration]

  final case class ClientConfiguration(serverHost: String, serverPort: Int)

  val layer: Layer[ReadError[String], Has[Configuration]] =
    TypesafeConfig.fromDefaultLoader(serverConfigurationDescriptor)
}

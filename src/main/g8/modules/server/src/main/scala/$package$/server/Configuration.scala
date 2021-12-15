package $package$.server

import $package$.server.Configuration.ServerConfiguration
import zio.config._
import zio.{Has, Layer}
import zio.config.typesafe.TypesafeConfig
import zio.config.magnolia.DeriveConfigDescriptor.descriptor

case class Configuration(server: ServerConfiguration)

object Configuration {
  val serverConfigurationDescriptor = descriptor[Configuration]

  final case class ServerConfiguration(port: Int)

  val layer: Layer[ReadError[String], Has[Configuration]] =
    TypesafeConfig.fromDefaultLoader(serverConfigurationDescriptor)
}

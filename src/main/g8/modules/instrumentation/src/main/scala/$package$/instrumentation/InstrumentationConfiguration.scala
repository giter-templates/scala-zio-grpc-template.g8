package $package$.instrumentation

import $package$.instrumentation.InstrumentationConfiguration._
import zio.config.ReadError
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config.typesafe.TypesafeConfig
import zio.{Has, Layer}

final case class InstrumentationConfiguration(metrics: MetricsConfiguration, tracing: TracingConfiguration)

object InstrumentationConfiguration {
  final case class MetricsConfiguration(httpPort: Int)

  final case class TracingConfiguration(host: String, port: Int, serviceName: String)

  val instrumentationConfigurationDescriptor = descriptor[InstrumentationConfiguration]

  val layer: Layer[ReadError[String], Has[InstrumentationConfiguration]] =
    TypesafeConfig.fromDefaultLoader(instrumentationConfigurationDescriptor)
}

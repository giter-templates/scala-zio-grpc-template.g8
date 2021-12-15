package $package$.instrumentation

import $package$.instrumentation.InstrumentationConfiguration.MetricsConfiguration
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.HTTPServer
import zio._

import java.net.InetSocketAddress

object Metrics {
  type HttpExporter = Has[HTTPServer]

  val live: ZLayer[Any, Nothing, Has[CollectorRegistry]] = ZLayer.succeed(CollectorRegistry.defaultRegistry)

  val httpExporter: ZLayer[Has[CollectorRegistry] with Has[InstrumentationConfiguration], Nothing, HttpExporter] =
    (for {
      config <- ZIO.service[InstrumentationConfiguration].toManaged_
      registry <- ZIO.service[CollectorRegistry].toManaged_
      server <- makeHttpServer(registry, config.metrics)
    } yield server).toLayer.orDie

  private def makeHttpServer(
    registry: CollectorRegistry,
    config: MetricsConfiguration
  ): ZManaged[Any, Throwable, HTTPServer] =
    ZManaged.make(
      Task.effect(new HTTPServer(new InetSocketAddress(config.httpPort), registry))
    )(server => Task.effect(server.stop()).orDie)
}

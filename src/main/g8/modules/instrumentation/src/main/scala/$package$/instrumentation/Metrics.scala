package $package$.instrumentation

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.HTTPServer
import zio._
import zio.metrics.prometheus.Registry

import java.net.InetSocketAddress

object Metrics {
  type HttpExporter = Has[HTTPServer]

  val live: ZLayer[Any, Nothing, Registry] = Registry.live

  val httpExporter: ZLayer[Registry, Nothing, HttpExporter] =
    (for {
      prometheus <- ZIO.service[Registry.Service].toManaged_
      registry <- prometheus.getCurrent().toManaged_
      server <- makeHttpServer(registry)
    } yield server).toLayer.orDie

  private def makeHttpServer(
    registry: CollectorRegistry
  ): ZManaged[Any, Throwable, HTTPServer] =
    ZManaged.make(
      Task.effect(new HTTPServer(new InetSocketAddress(8081), registry))
    )(server => Task.effect(server.stop()).orDie)
}

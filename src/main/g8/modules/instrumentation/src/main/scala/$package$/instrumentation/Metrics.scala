package $package$.instrumentation

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.HTTPServer
import zio._

import java.net.InetSocketAddress

object Metrics {
  type HttpExporter = Has[HTTPServer]

  val live: ZLayer[Any, Nothing, Has[CollectorRegistry]] = ZLayer.succeed(CollectorRegistry.defaultRegistry)

  val httpExporter: ZLayer[Has[CollectorRegistry], Nothing, HttpExporter] =
    (for {
      registry <- ZIO.service[CollectorRegistry].toManaged_
      server <- makeHttpServer(registry)
    } yield server).toLayer.orDie

  private def makeHttpServer(
    registry: CollectorRegistry
  ): ZManaged[Any, Throwable, HTTPServer] =
    ZManaged.make(
      Task.effect(new HTTPServer(new InetSocketAddress(8081), registry))
    )(server => Task.effect(server.stop()).orDie)
}

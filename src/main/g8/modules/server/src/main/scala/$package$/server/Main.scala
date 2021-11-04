package $package$.server

import common.{Metrics, Tracing}
import io.grpc.ServerBuilder
import io.jaegertracing.internal.JaegerTracer
import scalapb.zio_grpc.{ServerLayer, ServiceList}
import zio._
import zio.console.Console

object Main extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = (
    for {
      tracer <- ZIO.service[JaegerTracer]
      _ <- server(tracer).useForever
    } yield ()
  ).provideCustomLayer(
    Console.live ++ Metrics.live >+> Metrics.httpExporter ++ Tracing.jaeger("grpc_server")
  ).exitCode

  private def server(jaeger: JaegerTracer) = {
    val builder: ServerBuilder[_] = ServerBuilder.forPort(8080)
    val pingService = new PingServiceLive()

    builder.intercept(new MeteredServerInterceptor)
    builder.intercept(new TracedServerInterceptor(jaeger))

    ServerLayer
      .fromServiceList[Console](
        builder,
        ServiceList.add[Console, PingServiceLive](pingService)
      )
      .build
  }
}
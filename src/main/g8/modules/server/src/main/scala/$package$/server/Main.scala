package $package$.server

import $package$.instrumentation.{Metrics, Tracing, InstrumentationConfiguration}
import io.grpc.ServerBuilder
import io.jaegertracing.internal.JaegerTracer
import scalapb.zio_grpc.{ServerLayer, ServiceList}
import zio._
import zio.console.Console

object Main extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = (
    for {
      tracer <- ZIO.service[JaegerTracer]
      configuration <- ZIO.service[Configuration]
      _ <- server(tracer, configuration).useForever
    } yield ()
  ).provideCustomLayer {
    val serverConfig = Configuration.layer
    val instrumentationConfig = InstrumentationConfiguration.layer
    val metrics = (instrumentationConfig ++ Metrics.live) >+> Metrics.httpExporter
    val tracing = instrumentationConfig >>> Tracing.jaeger

    serverConfig ++ Console.live ++ metrics ++ tracing
  }.exitCode

  private def server(jaeger: JaegerTracer, configuration: Configuration) = {
    val builder: ServerBuilder[_] = ServerBuilder.forPort(configuration.server.port)
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

package $package$.client

import $package$.instrumentation.{Metrics, Tracing}
import $package$.protobuf.service.PingRequest
import $package$.protobuf.service.ZioService.PingServiceClient
import io.grpc.ManagedChannelBuilder
import io.jaegertracing.internal.JaegerTracer
import scalapb.zio_grpc.ZManagedChannel
import zio._
import zio.console._
import zio.duration._

object Main extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = (for {
    client <- ZIO.service[PingServiceClient.ZService[Any, Any]]
    _ <- program(client).repeat(Schedule.spaced(1.seconds)).forever.tapError(err => ZIO.effect(println(err)))
  } yield ())
    .provideCustomLayer(
      Console.live ++ Metrics.live >>> Metrics.httpExporter ++ Tracing.jaeger("grpc-client") >>> client
    )
    .exitCode

  def program(client: PingServiceClient.ZService[Any, Any]): ZIO[Console, Any, Unit] =
    for {
      _ <- putStrLn("Ping")
      _ <- client.ping(PingRequest())
      _ <- putStrLn("Pong")
    } yield ()

  val client: ZLayer[Has[JaegerTracer], Nothing, Has[PingServiceClient.ZService[Any, Any]]] =
    (for {
      tracer <- ZIO.service[JaegerTracer].toManaged_
      builder <- ZIO.effect {
        val builder: ManagedChannelBuilder[_] =
          ManagedChannelBuilder.forAddress("localhost", 8080)
        builder.usePlaintext()
        builder.intercept(new MeteredClientInterceptor)
        builder.intercept(new TracedClientInterceptor(tracer))

        builder
      }.toManaged_
      client <- PingServiceClient.managed[Any, Any](ZManagedChannel(builder))
    } yield client).toLayer.orDie
}

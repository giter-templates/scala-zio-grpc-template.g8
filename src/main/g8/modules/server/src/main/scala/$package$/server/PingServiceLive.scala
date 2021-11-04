package $package$.server

import com.google.protobuf.empty.Empty
import $package$.protobuf.service.ping_service.PingRequest
import $package$.protobuf.service.ping_service.ZioPingService.ZPingService
import io.grpc.Status
import zio.ZIO
import zio.console.Console
import zio.console._

class PingServiceLive extends ZPingService[Console, Any] {
  override def ping(request: PingRequest): ZIO[Console, Status, Empty] = for {
    _ <- putStrLn("Ping request").orElseFail(Status.INTERNAL)
  } yield Empty()
}
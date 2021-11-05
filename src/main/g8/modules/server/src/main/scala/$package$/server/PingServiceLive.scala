package $package$.server

import $package$.protobuf.service.PingRequest
import $package$.protobuf.service.ZioService.ZPingService
import com.google.protobuf.empty.Empty
import io.grpc.Status
import zio.ZIO
import zio.console.Console
import zio.console._

class PingServiceLive extends ZPingService[Console, Any] {
  override def ping(request: PingRequest): ZIO[Console, Status, Empty] = for {
    _ <- putStrLn("Ping request")
  } yield Empty()
}

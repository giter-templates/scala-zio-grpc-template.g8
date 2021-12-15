# scala-zio-grpc-template.g8

zio-grpc template with basic metrics (prometheus) and tracing (jaeger).

## Defaults

- sbt_scalafmt_plugin_version=2.4.3
- sbt_native_packager_plugin_version=1.9.2
- sbt_protoc_plugin_version=1.0.3
- zio_codegen_plugin_version=0.5.0

## Additional dependencies
- scalatest:3.2.8
- prometheus-simpleclient:0.11.0
- prometheus-simpleclient-httpserver:0.11.0
- jaeger-client:1.6.0
- zio-config:1.0.5

## Usage
```shell
sbt new giter-templates/scala-zio-grpc-template.g8
```

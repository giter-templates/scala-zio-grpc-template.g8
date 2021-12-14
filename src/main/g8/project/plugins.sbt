addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "$sbt_scalafmt_plugin_version$")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "$sbt_native_packager_plugin_version$")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "$sbt_protoc_plugin_version$")

libraryDependencies ++= Seq("com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "$zio_codegen_plugin_version$")


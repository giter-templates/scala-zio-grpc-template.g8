addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.2")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")

libraryDependencies ++= Seq("com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.5.0")


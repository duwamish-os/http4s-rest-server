name := "http4s-hello-server"

version := "1.0"

scalaVersion := "2.12.5"

val http4sVersion = "0.18.4"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion)

scalacOptions ++= Seq("-Ypartial-unification")

assemblyJarName in assembly := s"http4s-hello-server.jar"

mainClass in(Compile, run) := Some("server.Server")

name := "http4s-rest-server"

version := "1.0"

scalaVersion := "2.12.6"

val http4sVersion = "0.18.12"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.9.3",
  "org.http4s" %% "rho-core" % "0.18.0",
  "org.http4s" %% "rho-swagger" % "0.18.0",
)

scalacOptions ++= Seq("-Ypartial-unification")

assemblyJarName in assembly := s"http4s-rest-server.jar"

mainClass in(Compile, run) := Some("server.RestServer")

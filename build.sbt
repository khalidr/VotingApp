name := "VotingApp"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val sprayVersion = "1.3.3"
  val akkaVersion = "2.4.1"
  val reactiveMongoVersion = "0.11.9"

  List(
    "io.spray"                    %% "spray-routing"                  % sprayVersion,
    "io.spray"                    %% "spray-client"                   % sprayVersion,
    "io.spray"                    %% "spray-testkit"                  % sprayVersion  ,
    "com.typesafe.akka"           %% "akka-actor"                     % akkaVersion,
    "com.typesafe.akka"           %% "akka-testkit"                   % akkaVersion,
    "com.typesafe.akka"           %% "akka-slf4j"                     % akkaVersion,
    "org.mockito"                  % "mockito-core"                   % "1.10.19" % Test,
    "com.typesafe.play"           %% "play-json"                      % "2.3.10",
    "com.typesafe.scala-logging"  %% "scala-logging"                  % "3.1.0",
    "ch.qos.logback"               % "logback-classic"                % "1.1.3" % Runtime,
    "org.scalatest"               %% "scalatest"                      % "2.2.6" % Test,
    "org.scalaz"                  %% "scalaz-core"                    % "7.2.0",
    "org.reactivemongo"           %% "reactivemongo"                  % reactiveMongoVersion,
    "org.reactivemongo"           %% "play2-reactivemongo"            % reactiveMongoVersion,
    "de.flapdoodle.embed"          % "de.flapdoodle.embed.mongo"      % "1.50.0"     % Test
  )
}

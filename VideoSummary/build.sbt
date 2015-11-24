name := """VideoSummary"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)


scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "junit" % "junit" % "4.11",
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.2",
  "org.jsoup" % "jsoup" % "1.8.3",
  "com.github.detro" % "phantomjsdriver" % "1.2.0",
  "org.jgrapht" % "jgrapht-core" % "0.9.1",
  "mysql" % "mysql-connector-java" % "5.1.35",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2",
  "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.opennlp-asl" % "1.7.0",
  "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.languagetool-asl" % "1.7.0",
  "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.maltparser-asl" % "1.7.0",
  "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.io.text-asl" % "1.7.0",
  "de.tudarmstadt.ukp.dkpro.core" % "de.tudarmstadt.ukp.dkpro.core.io.conll-asl" % "1.7.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
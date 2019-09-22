enablePlugins(TutPlugin)

scalaVersion in ThisBuild := "2.12.10"

tutSourceDirectory := baseDirectory.value / "slides"
watchSources += baseDirectory.value / "slides"
tut := {
	val r = tut.value
	val revealFolder = baseDirectory.value / "reveal.js"
	tutTargetDirectory.value.listFiles().foreach(
		file => Sync.copy(file, revealFolder / file.name)
	)
	r
}
tutQuick := {
	val r = tutQuick.value
	val revealFolder = baseDirectory.value / "reveal.js"
	tutTargetDirectory.value.listFiles().foreach(
		file => Sync.copy(file, revealFolder / file.name)
	)
	r
}
scalacOptions.in(Tut) ~= filterConsoleScalacOptions


libraryDependencies ++= Seq(
	"io.higherkindness" %% "droste-core" % "0.7.0",
	"org.typelevel" %% "cats-effect" % "2.0.0",
	"org.typelevel" %% "cats-core" % "2.0.0"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

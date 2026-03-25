plugins {
  kotlin("jvm") version "2.2.0" apply false
  id("dev.kikugie.stonecutter")
  id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
  id("net.neoforged.moddev") version "2.0.113" apply false
  id("me.modmuss50.mod-publish-plugin") version "1.1.+" apply false
}

stonecutter active "26.1-fabric"

stonecutter tasks {
  order("publishModrinth")
  order("publishCurseforge")
}


for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
  group = "publishing"
  dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}
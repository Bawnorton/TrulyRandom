pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.kikugie.dev/releases/")
    maven("https://maven.kikugie.dev/snapshots/")
  }
}

dependencyResolutionManagement {
  repositories {
    maven("https://maven.kikugie.dev/snapshots")
  }

  versionCatalogs {
    create("ft") { from("dev.kikugie.fletching-table:fletching-table.catalog:0.2-SNAPSHOT") }
  }
}

plugins {
  id("dev.kikugie.stonecutter") version "0.10+"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

stonecutter {
  create(rootProject) {
    fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
      loaders.forEach { version("$name-$it", mcVersion).buildscript = "build.$it.gradle.kts" }

    mc("26.1.2", loaders = listOf("fabric"))
    mc("26.3", loaders = listOf("fabric"))

    vcsVersion = "26.3-fabric"
  }
}

rootProject.name = "TrulyRandom"
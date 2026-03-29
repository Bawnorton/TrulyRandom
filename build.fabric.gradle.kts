import dev.kikugie.fletching_table.annotation.MixinEnvironment
import trulyrandom.utils.*

plugins {
    kotlin("jvm")
    `maven-publish`
    id("trulyrandom.common")
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.15"
}

repositories {
    mavenCentral()
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    implementation("net.fabricmc:fabric-loader:0.18.4")

    implementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")

    include(implementation("com.github.tomnelson:jungrapht-visualization:1.4")!!)
    include(implementation("com.github.tomnelson:jungrapht-layout:1.4")!!)
    include(implementation("org.jgrapht:jgrapht-core:1.5.2")!!)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/${minecraft}.classtweaker"))

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
        appendProjectPathToConfigName = false
    }

    runConfigs["client"].apply {
        programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
        name = "Fabric Client $minecraft"
    }

    runConfigs["server"].apply {
        name = "Fabric Server $minecraft"
    }

    afterEvaluate {
        runConfigs.configureEach {
            applyMixinDebugSettings(::vmArg, ::property)
        }
    }
}

tasks {
    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${mod("version")}"))
        dependsOn("build")
    }

    processResources {
        exclude("**/neoforge.mods.toml, **/mods.toml")
    }
}

fletchingTable {
    fabric {
        entrypointMappings.put("fabric-datagen", "net.fabricmc.fabric.api.datagen.v1.FabricDataGeneratorEntrypoint")
    }

    mixins.register("main") {
        mixin("default", "${mod("id")}.mixins.json")
        mixin("client", "${mod("id")}.client.mixins.json") {
            environment = MixinEnvironment.Env.CLIENT
        }
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod("group")}.${mod("id")}"
            artifactId = "${mod("id")}-$loader"
            version = "${mod("version")}+$minecraft"

            from(components["java"])
        }
    }
}

publishMods {
    val mrToken = providers.gradleProperty("MODRINTH_TOKEN")
    val cfToken = providers.gradleProperty("CURSEFORGE_TOKEN")

    type = STABLE
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").map { it.archiveFile.get() })

    displayName = "${mod("name")} Fabric ${mod("version")} for $minecraft"
    version = mod("version")
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add(loader)

    val compatibleVersionString = mod("compatible_versions")!!
    val compatibleVersions = compatibleVersionString.split(",").map { it.trim() }

    modrinth {
        projectId = property("publishing.modrinth") as String
        accessToken = mrToken
        minecraftVersions.addAll(compatibleVersions)
    }

    curseforge {
        projectId = property("publishing.curseforge") as String
        accessToken = cfToken
        minecraftVersions.addAll(compatibleVersions)
    }
}
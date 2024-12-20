import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`

    id("net.minecrell.plugin-yml.paper")
    id("xyz.jpenilla.run-paper")
}

val libs: VersionCatalog = the<VersionCatalogsExtension>().named("libs")
dependencies {
    compileOnly(libs.findLibrary("paper").orElseThrow())
    compileOnly(libs.findLibrary("commandapi-bukkit").orElseThrow())
    compileOnly(libs.findLibrary("commandapi-bukkit-kotlin").orElseThrow())

    implementation(libs.findLibrary("mccoroutine-folia").orElseThrow())
    implementation(libs.findLibrary("mccoroutine-folia-core").orElseThrow())
}

tasks.runServer {
    minecraftVersion("1.21.1")

    downloadPlugins {
        modrinth("packetevents", "2.7.0")
        modrinth("commandapi", "9.7.0")
    }
}

paper {
    name = project.name
    version = rootProject.findProperty("version") as String? ?: "undefined"

    foliaSupported = true
    apiVersion = "1.21"
    authors = listOf("OceJlot")

    serverDependencies {
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("CommandAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}
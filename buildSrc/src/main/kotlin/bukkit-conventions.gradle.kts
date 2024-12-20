import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("net.minecrell.plugin-yml.paper")
    id("xyz.jpenilla.run-paper")
}

tasks.runServer {
    minecraftVersion("1.21.4")
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
    }
}
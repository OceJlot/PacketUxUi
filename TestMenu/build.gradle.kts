plugins {
    kotlin("jvm") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.0"
}

group = "net.craftoriya"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":API"))
    implementation("com.github.retrooper:packetevents-spigot:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")

}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.1")
        downloadPlugins {
            modrinth("ViaVersion", "5.2.0")
            modrinth("ViaBackwards", "5.2.0")
            modrinth("ServerUtils", "3.5.4")
            hangar("TabTPS", "1.3.21")
        }
    }
    shadowJar {
        mergeServiceFiles()
        exclude("kotlin/**")
        exclude("kotlinx/coroutines/**")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

kotlin {
    jvmToolchain(21)
}



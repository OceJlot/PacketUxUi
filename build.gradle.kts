plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0-Beta2"
}

group = "net.craftoriya"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven  ("https://repo.codemc.io/repository/maven-releases/")
}

//tasks {
//    shadowJar {
//        exclude("kotlin/**")
//        mergeServiceFiles()
//    }
//}

dependencies {
    compileOnly ("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly ("com.github.retrooper:packetevents-spigot:2.6.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    runServer {
        minecraftVersion("1.21.1")
    }
}

kotlin {
    jvmToolchain(21)
}


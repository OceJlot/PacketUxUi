plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.craftoriya"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT") // Paper API dependency
    compileOnly("com.github.retrooper:packetevents-spigot:2.6.0") // PacketEvents dependency
    implementation(kotlin("stdlib-jdk8")) // Kotlin standard library
}

tasks {
    shadowJar {
        archiveClassifier.set("") // Prevent "-all" suffix in shadow JAR
        exclude("kotlin/**") // Exclude Kotlin runtime if already shaded elsewhere
        mergeServiceFiles() // Merge `META-INF` service files if needed
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain(21) // Use JDK 21
}
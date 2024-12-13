
plugins {
    kotlin("jvm") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id ("maven-publish")
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
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.6.0")
    compileOnly(kotlin("stdlib-jdk8"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        exclude("kotlin/**")
        mergeServiceFiles()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

        }
    }
}


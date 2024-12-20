import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    mergeServiceFiles()
    minimize()

    val base = "net.craftoriya.libs"
    val relocations = listOf(
        "com.github.shynixn.mccoroutine" to "$base.mccoroutine",
        "io.leangen.geantyref" to "$base.geantyref",
        "kotlin" to "$base.kotlin",
        "kotlinx" to "$base.kotlinx",
        "org.incendo.cloud" to "$base.cloud",
        "org.intellij" to "$base.intellij",
        "org.jetbrains" to "$base.jetbrains",
        "okhttp3" to "$base.okhttp3",
        "com.sksamuel.aedile" to "$base.caffeine",
        "com.google.gson" to "$base.gson",
    )

    relocations.forEach { (from, to) ->
        relocate(from, to)
    }
}
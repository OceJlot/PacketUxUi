plugins {
    `common-conventions`
    `bukkit-conventions`
}

dependencies {
    api(project(":API"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")

}



plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnly(libs.packetevents.api)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.plaintext)

    api(libs.fastutil)
    api(libs.coroutines)
}

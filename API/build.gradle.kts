plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnlyApi(libs.paper)
    compileOnlyApi(libs.packetevents)

    api(libs.fastutil)
    api(libs.coroutines)
}

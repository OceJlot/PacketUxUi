plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnlyApi(libs.paper)
    compileOnlyApi(libs.packetevents.bukkit)

    api(project(":packetuxui-api"))
}

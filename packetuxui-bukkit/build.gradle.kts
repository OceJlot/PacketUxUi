plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnlyApi(libs.paper)
    compileOnlyApi(libs.packetevents.bukkit)

    api(project(":packetuxui-api"))
}

configurations.runtimeClasspath {
    val fastUtil = libs.fastutil.get()
    exclude(fastUtil.group, fastUtil.name)
}
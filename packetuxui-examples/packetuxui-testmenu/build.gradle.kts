plugins {
    `common-conventions`
    `bukkit-conventions`
    `shadow-conventions`
}

dependencies {
    api(project(":packetuxui-bukkit"))
    compileOnly(libs.paper)

    api(libs.mccoroutine.folia)
    api(libs.mccoroutine.folia.core)

    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
}

paper {
    main = "net.craftoriya.TestMenu"
}



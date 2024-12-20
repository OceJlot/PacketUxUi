plugins {
    `common-conventions`
    `bukkit-conventions`
    `shadow-conventions`
}

dependencies {
    api(project(":packetuxui-bukkit"))

    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
}

paper {
    main = "net.craftoriya.TestMenu"
}



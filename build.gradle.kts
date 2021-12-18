plugins {
    java

    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.5.31"
}

allprojects {
    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://nexus.badbones69.com/repository/maven-releases/")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://jitpack.io")
    }
}

subprojects {

    apply {
        plugin("java")
        plugin("kotlin")
    }

    group = "me.badbones69"
    version = "2.0 Dev Build v1"

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC3")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
        compileOnly("net.milkbowl.vault:VaultAPI:1.7")

        implementation("de.tr7zw:item-nbt-api:2.9.0-SNAPSHOT")

        implementation("com.github.cryptomorin:XSeries:8.5.0.1")
    }
}
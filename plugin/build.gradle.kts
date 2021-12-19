import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    implementation(project(":api"))

    //implementation("me.badbones69:premiumhooks:1.2")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}[v${version}].jar")

        relocate("de.tr7zw.changeme.nbtapi", "me.badbones69.libs.nbtapi")

        doLast {
            copy {
                from("build/libs/${rootProject.name}[v${version}].jar")
                into("H:\\Development\\Server\\plugins")
            }
        }
    }

    withType<KotlinCompile>{
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}
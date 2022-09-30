plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.6.20"
    application
}

group = "de.miraculixx"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("net.dv8tion", "JDA", "5.0.0-alpha.19")
    implementation("com.github.minndevelopment", "jda-ktx","0.9.5-alpha.19")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.3")

    implementation("io.ktor", "ktor-client-core-jvm", "2.0.1")
    implementation("io.ktor", "ktor-client-cio", "2.0.1")

    implementation("org.slf4j", "slf4j-api", "1.7.36")
    implementation("org.slf4j", "slf4j-simple", "1.7.36")

    implementation("org.yaml", "snakeyaml", "1.21")

    implementation("org.mariadb.jdbc", "mariadb-java-client", "3.0.5")

    implementation("com.github.twitch4j","twitch4j","1.12.0")
    implementation(group = "com.github.philippheuer.events4j", name = "events4j-handler-simple", version = "0.9.8")
}

application {
    mainClass.set("de.miraculixx.mcord_event.MainKt")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "de.miraculixx.mcord_event.MainKt"
        }
    }
}
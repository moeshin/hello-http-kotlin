plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.moeshin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map(::zipTree))
}

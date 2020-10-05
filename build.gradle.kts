@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "1.4.10" apply false
    id("org.jetbrains.dokka") version "1.4.10" apply false
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

subprojects {
    group = project.property("GROUP").toString()
    version = project.property("VERSION_NAME").toString()

    buildDir = File(rootProject.buildDir, name)

    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-progressive", "-Xjvm-default=enable")
            }
        }

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            withSourcesJar()
        }
    }

    afterEvaluate {
        configurations.configureEach {
            // There could be transitive dependencies in tests with a lower version. This could cause
            // problems with a newer Kotlin version that we use.
            resolutionStrategy.force(Dependencies.Kotlin.reflect)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.common)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.jdk8)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.jdk7)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.jdk6)
        }
    }
}
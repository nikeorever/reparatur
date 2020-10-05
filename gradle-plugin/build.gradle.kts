plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")

    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"
}

dependencies {
    implementation(project(":bytecode-writer"))
    implementation(Dependencies.jarTransformer)
    compileOnly(Dependencies.Android.gradlePlugin)
}

repositories {
    google()
    gradlePluginPortal()
}

// Use java-gradle-plugin to generate plugin descriptors and specify plugin ids
gradlePlugin {
    plugins {
        create("reparaturPlugin") {
            id = "cn.nikeo.reparatur"
            implementationClass = "cn.nikeo.reparatur.gradle.ReparaturPlugin"
        }
    }
}

pluginBundle {
    (plugins) {
        "reparaturPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = project.property("POM_NAME").toString()

            website = project.property("POM_URL").toString()
            vcsUrl = project.property("POM_SCM_URL").toString()

            tags = listOf("Android", "Lollipop", "API 22", "5.1.1", "WebView", "Crash")
            description = project.property("POM_DESCRIPTION").toString()
        }
    }
}

val dokkaDir = "${buildDir}/dokka"

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
    outputDirectory.set(file(dokkaDir))
}

task<Jar>("dokkaJar") {
    archiveClassifier.set("javadoc")
    from(dokkaDir)
    dependsOn("dokkaHtml")
}

apply(from = "$rootDir/gradle/gradle-mvn-push.gradle.kts")


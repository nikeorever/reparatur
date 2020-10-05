apply(plugin = "org.gradle.maven-publish")
apply(plugin = "org.gradle.signing")

val isReleaseBuild: Boolean get() = !version.toString().endsWith("-SNAPSHOT")

configure<PublishingExtension> {
    repositories {
        maven {
            url = uri(
                if (isReleaseBuild) {
                    "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                } else {
                    "https://oss.sonatype.org/content/repositories/snapshots"
                }
            )

            credentials {
                username = if (project.hasProperty("SONATYPE_NEXUS_USERNAME")) {
                    project.property("SONATYPE_NEXUS_USERNAME").toString()
                } else {
                    ""
                }
                password = if (project.hasProperty("SONATYPE_NEXUS_PASSWORD")) {
                    project.property("SONATYPE_NEXUS_PASSWORD").toString()
                } else {
                    ""
                }
            }
        }
    }

    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            artifact(project.tasks.getByName("dokkaJar"))

            pom {
                name.set(project.property("POM_NAME").toString())
                artifactId = project.property("POM_ARTIFACT_ID").toString()
                packaging = project.property("POM_PACKAGING").toString()

                description.set(project.property("POM_DESCRIPTION").toString())
                inceptionYear.set(project.property("POM_INCEPTION_YEAR").toString())


                url.set(project.property("POM_URL").toString())

                scm {
                    url.set(project.property("POM_SCM_URL").toString())
                    connection.set(project.property("POM_SCM_CONNECTION").toString())
                    developerConnection.set(project.property("POM_SCM_DEV_CONNECTION").toString())
                }
                licenses {
                    license {
                        name.set(project.property("POM_LICENCE_NAME").toString())
                        url.set(project.property("POM_LICENCE_URL").toString())
                        distribution.set(project.property("POM_LICENCE_DIST").toString())
                    }
                }
                developers {
                    developer {
                        id.set("xianxueliang")
                        name.set("xianxueliang")
                    }
                }
            }
        }
    }
}

if (isReleaseBuild) {
    configure<SigningExtension> {
        sign(the<PublishingExtension>().publications["release"])
    }
}

tasks.register("publishSnapshot") {
    if (!isReleaseBuild) {
        dependsOn(tasks.getByName("publish"))
    }
}

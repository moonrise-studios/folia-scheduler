plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version("9.2.2")
}

var id = "folia-scheduler"
var domain = "gg.moonrise.scheduler"
var apiVersion = "1.0.0"

repositories {
    mavenCentral()

    // PaperMC
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveBaseName.set(id)
    archiveVersion.set(apiVersion)
    archiveClassifier.set("")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["shadow"])
            artifact(tasks.named<Jar>("sourcesJar"))
            artifact(tasks.named<Jar>("javadocJar"))

            groupId = domain
            artifactId = id
            version = apiVersion

            pom {
                name.set(id)
                description.set(project.description)
                url.set("https://github.com/moonrise-studios/folia-scheduler")

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("ericlmao")
                        name.set("Eric")
                    }
                }
            }
        }
    }
}

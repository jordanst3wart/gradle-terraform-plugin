pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    fun getEnvOrThrow(environmentVariable: String): String =
        System.getenv(environmentVariable) ?: throw InvalidUserDataException("Please provide \"$environmentVariable\" environment variable")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // need gradle plugins as a dependency repository
        maven {
            url = uri("https://maven.pkg.github.com/jordanst3wart/gradle-terraform-plugin")
            // need credentials despite being public
            credentials {
                username = getEnvOrThrow("GH_USERNAME")
                password = getEnvOrThrow("GH_PACKAGES_TOKEN")
            }
        }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = ("plugin-build")
include(":plugin")

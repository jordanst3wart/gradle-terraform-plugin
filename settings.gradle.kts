pluginManagement {
    fun getEnvOrThrow(environmentVariable: String): String =
        System.getenv(environmentVariable) ?: throw InvalidUserDataException("Please provide \"$environmentVariable\" env varname")

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/jordanst3wart/gradle-terraform-plugin")
            credentials {
                username = getEnvOrThrow("GH_USERNAME")
                password = getEnvOrThrow("GH_PACKAGES_TOKEN")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

rootProject.name = "gradle-terraform-plugin"
include("plugin")
include(":example")

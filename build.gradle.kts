import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ktlint) apply true
    idea
}

idea.project.languageLevel = IdeaLanguageLevel("JDK_17")

subprojects {
    // group = property("GROUP").toString()

    apply {
        plugin(
            rootProject.libs.plugins.ktlint
                .get()
                .pluginId,
        )
    }

    ktlint {
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
        reporters {
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.JSON)
        }
    }
}

tasks.register("clean", Delete::class.java) {
    group = "build"
    delete(rootProject.layout.buildDirectory)
}

tasks.register("reformatAll") {
    group = "formatting"
    description = "Reformat all the Kotlin Code"

    dependsOn("ktlintFormat")
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:ktlintFormat"))
}

tasks.register("check") {
    group = "verification"
    // dependsOn(":example:check")
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:check"))
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:validatePlugins"))
}

tasks.register("test") {
    group = "verification"
    // could include example:test
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:test"))
}

tasks.register("build") {
    group = "build"
    // could include example:build
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:build"))
}

tasks.register("assemble") {
    group = "build"
    // could include example:assemble
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:assemble"))
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
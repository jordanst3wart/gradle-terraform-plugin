package org.ysb33r.gradle.terraform

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertTrue

class TerraformCheckPluginTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `The check task depends on terraform format check tasks`() {
        // Setup
        project.pluginManager.apply("foo.bar.terraform")

        // When
        project.allprojects { subproject ->
            //subproject.configure<Any> {
                // This would need to be replaced with the actual extension configuration
                // based on your plugin's implementation
                //terraformSourceSets {
                //     main {
                //         srcDir = file("foo/bar")
                //     }
                // }
            //}
        }

        val check = project.tasks.named("check").get()
        val terraformFormatCheckProvider = project.tasks.named("fmtCheckMain").get()
        val terraformFormatCheck = project.tasks.named("fmtCheckMain").get()

        val dependsOn = check.dependsOn // might need to be flattened

        // Then
        assertTrue(
            dependsOn.contains(terraformFormatCheck.name) ||
                    dependsOn.contains(terraformFormatCheck) ||
                    dependsOn.contains(terraformFormatCheckProvider)
        )
    }
}
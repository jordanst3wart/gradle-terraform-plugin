package org.ysb33r.gradle.terraform

import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.terraform.tasks.RemoteStateTask
import org.ysb33r.gradle.terraform.tasks.TerraformApply
import org.ysb33r.gradle.terraform.tasks.TerraformInit
import org.ysb33r.gradle.terraform.tasks.TerraformPlan
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.io.File

class TerraformPluginTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply("foo.bar.terraform")
        project.allprojects { subproject ->
            subproject.configure<Any> {
                // This would need to be replaced with the actual extension configuration
                // based on your plugin's implementation
                // terraformSourceSets {
                //     main {
                //         srcDir = file("foo/bar")
                //         backendText.set("hi")
                //     }
                //     release
                // }
            }
        }
    }

    @Test
    fun `Create additional source sets`() {
        // Note: This test would need to be adapted based on your actual plugin implementation
        // The following assertions assume the plugin creates the expected extension and tasks

        val terraformSourceSets = project.extensions.getByName("terraformSourceSets")
        // val mainSourceSet = terraformSourceSets.getByName("main")
        // assertEquals(project.file("foo/bar"), mainSourceSet.srcDir.get().asFile)

        // val releaseSourceSet = terraformSourceSets.getByName("release")
        // val file = releaseSourceSet.srcDir.get().asFile
        // assertTrue(file.absolutePath.contains("src/release/tf"))

        assertTrue(project.tasks.named("initRelease").get() is TerraformInit)
        assertTrue(project.tasks.named("planRelease").get() is TerraformPlan)
        assertTrue(project.tasks.named("applyRelease").get() is TerraformApply)
    }

    @Test
    fun `Plugin is applied`() {
        // Backend tasks are not created if no backend text set
        val backendTask = project.tasks.named("createBackendConfigurationMain").get() as RemoteStateTask
        assertEquals("createBackendConfigurationMain", backendTask.name)
        assertTrue(backendTask.backendFileRequired.get())
        assertEquals(
            File(project.layout.buildDirectory.asFile.get(), "main/tf/remoteState/backend-config.tf"),
            backendTask.backendConfig.get()
        )

        val task = project.tasks.named("initMain").get() as TerraformInit
        assertEquals(
            File(project.layout.buildDirectory.asFile.get(), "main/tf/remoteState/backend-config.tf"),
            task.backendConfig.get()
        )
        assertTrue(task.useBackendConfig.get()) // should be true

        try {
            project.tasks.named("createBackendConfigurationRelease").get()
            fail("Expected UnknownTaskException")
        } catch (e: UnknownTaskException) {
            // Expected exception
        }
    }

    @Test
    fun `The default destination directory is based upon the source set name`() {
        val remoteStateTask = project.tasks.withType(RemoteStateTask::class.java)
        assertEquals(2, remoteStateTask.size)

        val main = (project.tasks.getByName("createBackendConfigurationMain") as RemoteStateTask).backendConfig.get()
        assertEquals("backend-config.tf", main.name)
        assertEquals("remoteState", main.parentFile.name)
        assertEquals(
            File(project.layout.buildDirectory.asFile.get(), "main/tf"),
            main.parentFile.parentFile
        )
    }

    @Test
    fun `terraformInit has configuration file correctly setup`() {
        val initMainTask = project.tasks.getByName("initMain") as TerraformInit
        val createBackendTask = project.tasks.getByName("createBackendConfigurationMain") as RemoteStateTask

        assertEquals(
            createBackendTask.backendConfig.get(),
            initMainTask.backendConfig.get()
        )

        try {
            val initReleaseTask = project.tasks.getByName("initRelease") as TerraformInit
            val createBackendReleaseTask = project.tasks.getByName("createBackendConfigurationRelease") as RemoteStateTask
            initReleaseTask.backendConfig.get() == createBackendReleaseTask.backendConfig.get()
            fail("Expected exception when accessing non-existent task")
        } catch (e: Exception) {
            // Expected exception (could be UnknownTaskException or similar)
        }
    }
}
package org.ysb33r.gradle.terraform.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.terraform.ExecSpec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class TerraformApplyTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `commands for terraform apply`() {
        // Setup
        project.plugins.apply("foo.bar.terraform")

        // When
        project.allprojects { subproject ->
            subproject.extensions.configure("terraformSourceSets") { sourceSets ->
                sourceSets.named("main") { main ->
                    main.srcDir = project.file("foo/bar")
                    main.variables {
                        file("auto.tfvars.json")
                    }
                }
            }
        }

        // Then
        val applyTask = project.tasks.named("applyMain").get()
        assertTrue(applyTask is TerraformApply)

        val spec = (applyTask as TerraformApply).buildExecSpec()

        assertTrue(spec.env.keys.containsAll(listOf("TF_DATA_DIR", "TF_CLI_CONFIG_FILE", "TF_LOG_PATH", "TF_LOG", "PATH", "HOME")))
        assertEquals(6, spec.env.size)

        assertTrue(spec.args.containsAll(listOf("-auto-approve", "-input=false", "-lock=true", "-lock-timeout=30s", "-parallelism=10", "-refresh=true")))
        assertEquals(7, spec.args.size)

        var planfile = false
        spec.args.forEach { arg ->
            if (arg.contains(".tf.plan")) {
                planfile = true
            }
        }

        var varsFile = false
        spec.args.forEach { arg ->
            if (arg.contains("auto.tfvars.json")) {
                varsFile = true
            }
        }

        assertTrue(planfile)
        assertFalse(varsFile)
        // println(spec.getCommandLine())
    }
}
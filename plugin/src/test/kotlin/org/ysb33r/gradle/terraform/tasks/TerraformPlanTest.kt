package org.ysb33r.gradle.terraform.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.terraform.ExecSpec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class TerraformPlanTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `commands for terraform plan`() {
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
        val task = project.tasks.named("planMain").get()
        assertTrue(task is TerraformPlan)

        val spec = (task as TerraformPlan).buildExecSpec()

        assertTrue(spec.env.keys.containsAll(listOf("TF_DATA_DIR", "TF_CLI_CONFIG_FILE", "TF_LOG_PATH", "TF_LOG", "PATH", "HOME")))
        assertEquals(6, spec.env.size)

        assertTrue(spec.args.containsAll(listOf("-input=false", "-lock=true", "-lock-timeout=30s", "-parallelism=10", "-refresh=true", "-detailed-exitcode")))
        assertEquals(9, spec.args.size)

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
        assertTrue(varsFile)
        // println(spec.getCommandLine())
    }
}
package org.ysb33r.gradle.terraform.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.terraform.ExecSpec
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class TerraformDestroySpec {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `commands for terraform destroy`() {
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
        val task = project.tasks.named("destroyMain").get()
        assertTrue(task is TerraformDestroy)
        // task.setTargets(listOf("someResource")) // bug

        val spec = (task as TerraformDestroy).buildExecSpec()

        assertTrue(spec.env.keys.containsAll(listOf("TF_DATA_DIR", "TF_CLI_CONFIG_FILE", "TF_LOG_PATH", "TF_LOG", "PATH", "HOME")))
        assertEquals(6, spec.env.size)

        assertTrue(spec.args.containsAll(listOf(
            "-auto-approve",
            "-input=false",
            "-lock=true",
            "-lock-timeout=30s",
            "-parallelism=10",
            "-refresh=true"
        )))
        assertEquals(8, spec.args.size)

        var varsFile = false
        spec.args.forEach { arg ->
            if (arg.contains("auto.tfvars.json")) {
                varsFile = true
            }
        }
        assertTrue(varsFile)

        // NOTE can't use a plan file
        /*
        var planfile = false
        spec.getCmdArgs().forEach { arg ->
            if (arg.contains(".tf.destroy.plan")) {
                planfile = true
            }
        }
        assertTrue(planfile)
        */
        // println(spec.getCommandLine())
    }
}
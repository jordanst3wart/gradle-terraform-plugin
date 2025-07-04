package org.ysb33r.gradle.terraform.plugins

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.ysb33r.gradle.terraform.TerraformExtension
import org.ysb33r.gradle.terraform.TerraformSetupExtension
import org.ysb33r.gradle.terraform.TerraformSourceSet
import org.ysb33r.gradle.terraform.internal.Convention
import org.ysb33r.gradle.terraform.internal.Convention.createTasksByConvention
import org.ysb33r.gradle.terraform.internal.Convention.taskName
import org.ysb33r.gradle.terraform.tasks.DefaultTerraformTasks
import org.ysb33r.gradle.terraform.tasks.DefaultTerraformTasks.FMT_APPLY
import org.ysb33r.gradle.terraform.tasks.RemoteStateTask
import org.ysb33r.gradle.terraform.tasks.TerraformFmtCheck
import org.ysb33r.gradle.terraform.tasks.TerraformSetup
import org.ysb33r.gradle.terraform.tasks.TerraformTask
import org.ysb33r.gradle.terraform.tasks.TerraformValidate

/**
 * Provide the basic capabilities for dealing with Terraform tasks. Allow for downloading & caching of
 * Terraform distributions on a variety of the most common development platforms.
 */
class TerraformPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        registerSetupTerraformTask(project)

        project.tasks.withType(RemoteStateTask::class.java).configureEach { t ->
            t.group = Convention.TERRAFORM_TASK_GROUP
            t.dependsOn(TerraformSetupExtension.TERRAFORM_SETUP_TASK)
        }

        project.tasks.withType(TerraformTask::class.java).configureEach { t ->
            t.dependsOn(TerraformSetupExtension.TERRAFORM_SETUP_TASK)
        }

        project.extensions.create(TerraformExtension.NAME, TerraformExtension::class.java, project)
        val sourceSetContainer = createTerraformSourceSetsExtension(project)

        val formatAll = project.tasks.register(Convention.FORMAT_ALL)
        formatAll.configure {
            it.group = Convention.TERRAFORM_TASK_GROUP
            it.description = "Formats all terraform source"
        }
        terraformSourceSetConventionTaskRules(project, sourceSetContainer, formatAll)
        configureCheck(project)
        configureRootTask(project)
    }

    companion object {
        private fun registerSetupTerraformTask(project: Project) {
            val terraformSetupExt =
                project.extensions
                    .create(Convention.TERRAFORM_SETUP_EXT, TerraformSetupExtension::class.java, project)
            project.tasks.register(TerraformSetupExtension.TERRAFORM_SETUP_TASK, TerraformSetup::class.java) { task ->
                task.group = Convention.TERRAFORM_TASK_GROUP
                task.description = "Generates Terraform rc file, creates plugin cache directory, and downloads binary"
                task.terraformRcMap.set(terraformSetupExt.terraformRcMap)
                task.executable.set(terraformSetupExt.executable)
                // val executableObj = executable.get()
                // executableFile.set(executableObj.executablePath())
                // task.executableFile.set(terraformSetupExt.executable.get().executablePath())
            }
        }

        private fun createTerraformSourceSetsExtension(
            project: Project,
        ): NamedDomainObjectContainer<TerraformSourceSet> {
            val factory =
                NamedDomainObjectFactory { name ->
                    project.objects.newInstance(
                        TerraformSourceSet::class.java,
                        project,
                        name,
                    )
                }
            val sourceSetContainer =
                project.objects.domainObjectContainer(TerraformSourceSet::class.java, factory)
            project.extensions.add(Convention.TERRAFORM_SOURCESETS, sourceSetContainer)
            return sourceSetContainer
        }

        private fun terraformSourceSetConventionTaskRules(
            project: Project,
            sourceSetContainer: NamedDomainObjectContainer<TerraformSourceSet>,
            formatAll: TaskProvider<Task>,
        ) {
            sourceSetContainer.configureEach { sourceSet ->
                createTasksByConvention(project, sourceSet)
                formatAll.configure {
                    it.dependsOn(project.tasks.named(taskName(sourceSet.name, FMT_APPLY.command)))
                }
            }
        }

        private fun configureCheck(project: Project) {
            project.pluginManager.apply(LifecycleBasePlugin::class.java)
            val check = project.tasks.named(CHECK_TASK_NAME)
            check.configure {
                it.dependsOn(project.tasks.withType(TerraformFmtCheck::class.java))
                it.dependsOn(project.tasks.withType(TerraformValidate::class.java))
            }
        }

        // root tasks to run plans for all source sets
        private fun configureRootTask(project: Project) {
            DefaultTerraformTasks.tasks().forEach { task ->
                if (task == DefaultTerraformTasks.INIT) {
                    return@forEach // "init" can clash with "./gradlew init", or creating a new Gradle project
                    // "init" can also be handled by task dependency resolution
                }
                project.tasks.findByName(task.command)
                    ?: project.tasks.register(task.command) {
                        it.group = Convention.TERRAFORM_TASK_GROUP
                    }

                project.tasks.getByName(task.command) {
                    it.dependsOn(project.tasks.withType(task.type))
                }
            }
        }
    }
}
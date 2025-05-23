/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ysb33r.gradle.terraform

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.terraform.tasks.RemoteStateTask
import org.ysb33r.gradle.terraform.tasks.TerraformApply
import org.ysb33r.gradle.terraform.tasks.TerraformInit
import org.ysb33r.gradle.terraform.tasks.TerraformPlan
import spock.lang.Specification

class TerraformPluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void setup() {
        project.apply plugin: 'foo.bar.terraform'
        project.allprojects {
            terraformSourceSets {
                main {
                    srcDir = file('foo/bar')
                    backendText.set("hi")
                }
                release
            }
        }
    }

    void 'Create additional source sets'() {
        expect:
        def tss = project.terraformSourceSets
        tss.getByName('main').srcDir.get().asFile == project.file('foo/bar')
        def file = tss.getByName( 'release').srcDir.get().asFile as File
        file.absolutePath.contains('src/release/tf')
        project.tasks.named('initRelease').get() instanceof TerraformInit
        project.tasks.named('planRelease').get() instanceof TerraformPlan
        project.tasks.named('applyRelease').get() instanceof TerraformApply
    }

    void 'Plugin is applied'() {
        expect: 'Backend tasks are not created if no backend text set'
        def backendTask = project.tasks.named('createBackendConfigurationMain').get() as RemoteStateTask
        backendTask.name == 'createBackendConfigurationMain'
        backendTask.backendFileRequired.get() == true
        backendTask.backendConfig.get() == new File(project.layout.buildDirectory.asFile.get(), "main/tf/remoteState/backend-config.tf")

        def task = project.tasks.named('initMain').get() as TerraformInit
        task.backendConfig.get() == new File(project.buildDir, "main/tf/remoteState/backend-config.tf")
        task.useBackendConfig.get() == true // should be true
        try {
            project.tasks.named('createBackendConfigurationRelease').get()
            false == true
        } catch (org.gradle.api.UnknownTaskException e) {
        }
    }

    void 'The default destination directory is based upon the source set name'() {
        expect:
        def remoteStateTask = project.tasks.withType(RemoteStateTask)
        remoteStateTask.size() == 2
        File main = project.tasks.createBackendConfigurationMain.backendConfig.get()
        main.name == 'backend-config.tf'
        main.parentFile.name == 'remoteState'
        main.parentFile.parentFile == new File(project.buildDir, 'main/tf')
    }

    void 'terraformInit has configuration file correctly setup'() {
        expect:
        project.tasks.initMain.backendConfig.get() == project.tasks.createBackendConfigurationMain.backendConfig.get()

        try {
            project.tasks.initRelease.backendConfig.get() == project.tasks.createBackendConfigurationRelease.backendConfig.get()
            false == true
        } catch (groovy.lang.MissingPropertyException e) {
        }
    }
}
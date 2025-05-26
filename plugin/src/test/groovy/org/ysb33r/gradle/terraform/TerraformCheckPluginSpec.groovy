package org.ysb33r.gradle.terraform

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TerraformCheckPluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void 'The check task depends on tf*FmtCheck tasks'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'

        when:
        project.allprojects {
            terraformSourceSets {
                main {
                    srcDir = file('foo/bar')
                }
            }
        }

        def check = project.tasks.named('check').get()
        def tfFmtCheckProvider = project.tasks.named('fmtCheckMain').get()
        def tfFmtCheck = project.tasks.named('fmtCheckMain').get()

        def dependsOn = check.dependsOn.flatten()

        then:
        dependsOn.contains(tfFmtCheck.name) ||
            dependsOn.contains(tfFmtCheck) ||
            dependsOn.contains(tfFmtCheckProvider)
    }
}
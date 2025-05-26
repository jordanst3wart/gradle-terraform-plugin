package org.ysb33r.gradle.terraform

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

// TODO this will need to change...
class TerraformExtensionSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    def 'Configure terraform executable using a version'() {
        when: 'A version is configured'
        project.allprojects {
            apply plugin : 'foo.bar.terraform'

            // tag::configure-with-tag[]
            terraform {
                executable version : '1.0.3' // <1>
            }
            // end::configure-with-tag[]
        }

        then:
        project.terraform.resolvableExecutable != null
    }

    def 'Configure terraform executable using a path'() {
        when: 'A path is configured'
        project.allprojects {
            apply plugin : 'foo.bar.terraform'

            // tag::configure-with-path[]
            terraform {
                executable path : '/path/to/terraform' // <2>
            }
            // end::configure-with-path[]
        }

        then:
        project.terraform.resolvableExecutable != null
    }

    // TODO this is probably doing to fail soon
    def 'Cannot configure terraform with more than one option'() {
        setup:
        project.apply plugin : 'foo.bar.terraform'

        when:
        project.terraform.executable version : '7.10.0', path : '/path/to'

        then:
        thrown(GradleException)

        when:
        project.terraform.executable version : '7.10.0', search : '/path/to'

        then:
        thrown(GradleException)
    }
}
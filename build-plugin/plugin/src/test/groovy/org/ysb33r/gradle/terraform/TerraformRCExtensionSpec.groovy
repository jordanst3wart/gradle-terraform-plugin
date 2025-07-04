package org.ysb33r.gradle.terraform

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TerraformRCExtensionSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    /*def 'Configure terraform setup'() {
        setup:
        def os = OperatingSystem.current()

        when: 'A version is configured'
        project.allprojects {
            apply plugin: 'org.ysb33r.terraform'

            terraformrc {
                disableCheckPoint = true
                disableCheckPointSignature = false
            }
        }

        def terraformrc = project.extensions.getByType(TerraformSetupExtension)
        def hcl = terraformrc.toHCL(new StringWriter()).toString().replaceAll(~/\r?\n/, '!!')

        then:
        terraformrc.pluginCacheDir.get().asFile.absolutePath.contains('caches/terraform.d')
        hcl == """disable_checkpoint = true
disable_checkpoint_signature = false
plugin_cache_dir = "${escapedFilePath(os, terraformrc.pluginCacheDir.get().asFile)}"
plugin_cache_may_break_dependency_lock_file = false
""".replaceAll(~/\r?\n/, '!!')
    }*/
}
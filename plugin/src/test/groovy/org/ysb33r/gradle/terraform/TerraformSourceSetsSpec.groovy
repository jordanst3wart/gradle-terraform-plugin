package org.ysb33r.gradle.terraform

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.terraform.config.Variables
import spock.lang.Specification

class TerraformSourceSetsSpec extends Specification {
    Project project = ProjectBuilder.builder().build()

    void 'Variable definitions in source set should not create new source sets'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'

        when:
        project.allprojects {
            ext {
                myStr1 = 'bar1'
            }

            terraformSourceSets {
                main {
                    variables {
                        var 'foo1', myStr1
                    }
                }
            }
        }

        NamedDomainObjectContainer<TerraformSourceSet> tss = project.terraformSourceSets
        def variables = ((Variables) tss.getByName('main').vars)

        then:
        verifyAll {
            variables.vars.foo1 == 'bar1'
        }
    }

    void 'test different variables supported'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'

        when:
        Map<String, ?> myMap = [name: 'John', age: 30, city: 'New York']
        project.allprojects {
            terraformSourceSets {
                main {
                    variables {
                        var 'abc', 'abcde'
                        // TODO support
                        //map 'someMap' myMap
                        //list 'list' listOf('bar1', 'bar2')
                    }
                }
            }
        }

        NamedDomainObjectContainer<TerraformSourceSet> tss = project.terraformSourceSets
        def variables = ((Variables) tss.getByName('main').vars)

        then:
        verifyAll {
            variables.vars.abc == 'abcde'
            //allVars.vars.someMap.name == 'John'
        }
    }

    void 'Items must be able resolve entities in project scope'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'
        project.extensions.create('testExt', TestExtension, project.providers, project)

        when:
        configureFourSourceSets()

        then:
        noExceptionThrown()
    }

    void 'Items must be able resolve entities in project scope even with different order of plugins applied'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'
        project.extensions.create('testExt', TestExtension, project.providers, project)

        when:
        configureFourSourceSets()

        then:
        noExceptionThrown()
    }

    void 'Can provide a tfvars file'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'

        project.allprojects {
            terraformSourceSets {
                main {
                    variables {
                        file( 'foo.tfvars')
                        file( 'foo2.tfvars')
                    }
                }
            }
        }

        when:
        Variables vars = project.terraformSourceSets.getByName('main').vars
        final cmdline = vars.commandLineArgs
        final fooPos = cmdline.findIndexOf { it.endsWith('foo.tfvars') }
        final foo2Pos = cmdline.findIndexOf { it.endsWith('foo2.tfvars') }

        then:
        vars.fileNames.contains('foo.tfvars')
        // TODO fix this
        // cmdline.contains("-var-file=")
        // cmdline.contains('src/main/tf/foo.tfvars')

        and:
        fooPos < foo2Pos
    }

    void 'source sets'() {
        setup:
        project.apply plugin: 'foo.bar.terraform'
        project.allprojects {
            terraformSourceSets {
                main {
                    srcDir = file('foo/bar')
                    backendText.set("hi")
                    // should support backendFile...
                    // just variables files... these can be json or tfvars
                    variables {
                        file 'filename.tf'
                        file 'foo.tf'
                    }
                }
                release
            }
        }

        expect:
        def tss = project.terraformSourceSets
        def mainSourceSet = tss.getByName('main') as TerraformSourceSet
        mainSourceSet.srcDir.get().asFile == project.file('foo/bar')
        mainSourceSet.backendText.get() == "hi"
        //Set<String> files = ["filename.tf", "foo.tf"].toSet()
        //mainSourceSet.variables.fileNames == files

        def releaseSourceSet = tss.getByName('release')
        def file = releaseSourceSet.srcDir.get().asFile as File
        file.absolutePath.contains('src/release/tf')
    }

    void configureFourSourceSets() {
        project.allprojects {
            terraformSourceSets {
                main {
                }
                create('created') {
                }
                register('registered') {
                }
                groovyAutoAddStyle {
                }
            }
        }
    }

    static class TestExtension {
        final ProviderFactory providers
        final Project project1

        TestExtension(ProviderFactory p, Project p1) {
            this.providers = p
            this.project1 = p1
        }
    }
}
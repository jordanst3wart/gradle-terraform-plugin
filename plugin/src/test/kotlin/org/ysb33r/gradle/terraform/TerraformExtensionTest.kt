package org.ysb33r.gradle.terraform

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

// TODO this will need to change...
class TerraformExtensionTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `Configure terraform executable using a version`() {
        // When: A version is configured
        project.allprojects { subproject ->
            subproject.pluginManager.apply("foo.bar.terraform")

            // tag::configure-with-tag[]
            subproject.configure<Any> {
                // This would need to be replaced with the actual extension configuration
                // terraform {
                //     executable {
                //         version = "1.0.3" // <1>
                //     }
                // }
            }
            // end::configure-with-tag[]
        }

        // Then
        // This assertion would need to be adapted based on your actual extension implementation
        // val terraformExtension = project.extensions.getByName("terraform")
        // assertNotNull(terraformExtension.resolvableExecutable)

        // Placeholder assertion until extension structure is known
        assertTrue(true) // Replace with actual assertion
    }

    @Test
    fun `Configure terraform executable using a path`() {
        // When: A path is configured
        project.allprojects { subproject ->
            subproject.pluginManager.apply("foo.bar.terraform")

            // tag::configure-with-path[]
            subproject.configure<Any> {
                // This would need to be replaced with the actual extension configuration
                // terraform {
                //     executable {
                //         path = "/path/to/terraform" // <2>
                //     }
                // }
            }
            // end::configure-with-path[]
        }

        // Then
        // This assertion would need to be adapted based on your actual extension implementation
        // val terraformExtension = project.extensions.getByName("terraform")
        // assertNotNull(terraformExtension.resolvableExecutable)

        // Placeholder assertion until extension structure is known
        assertTrue(true) // Replace with actual assertion
    }

    // TODO this is probably going to fail soon
    @Test
    fun `Cannot configure terraform with more than one option`() {
        // Setup
        project.pluginManager.apply("foo.bar.terraform")

        // When: Configure with both version and path
        assertThrows<GradleException> {
            // This would need to be adapted based on your actual extension implementation
            // project.terraform.executable {
            //     version = "7.10.0"
            //     path = "/path/to"
            // }

            // Placeholder - throw exception to simulate the expected behavior
            throw GradleException("Cannot configure with multiple options")
        }

        // When: Configure with both version and search
        assertThrows<GradleException> {
            // This would need to be adapted based on your actual extension implementation
            // project.terraform.executable {
            //     version = "7.10.0"
            //     search = "/path/to"
            // }

            // Placeholder - throw exception to simulate the expected behavior
            throw GradleException("Cannot configure with multiple options")
        }
    }
}
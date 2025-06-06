/*import org.ysb33r.gradle.terraform.internal.Executable

plugins {
    // using local maven snapshot of the plugin
    // should be moved to includeBuild detailed here: https://docs.gradle.org/current/userguide/testing_gradle_plugins.html#manual-tests
    // builds to: ~/.m2/repository/foo/bar/terraform/foo.bar.terraform.gradle.plugin/INSTALLER3-SNAPSHOT
    id("org.ysb33r.terraform") version "TESTS-IN-KOTLIN-SNAPSHOT"
}

terraformSetup {
    val binary = Executable.TERRAFORM
    binary.version = "1.10.1" // TODO should be a nicer api call than this
    executable.set(binary)
}

terraform {
    useAwsEnvironment()
    useGoogleEnvironment()
    setLockTimeout(31)
    setParallel(11)
}

terraformSourceSets {
    create("main") {
        srcDir.set(File("src/main/tf"))
        backendText.set("# foo = bar") // TODO needs to be defined..., could be optional
    }
    create("aws") {
        srcDir.set(File("src/aws/tf"))
        backendText.set("# foo = bar") // TODO needs to be defined..., could be optional
    }
}

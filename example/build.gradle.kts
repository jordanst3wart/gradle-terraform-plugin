import org.ysb33r.gradle.terraform.internal.Executable

plugins {
    id("org.ysb33r.terraform")
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

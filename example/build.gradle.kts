import org.ysb33r.gradle.terraform.internal.Executable

plugins {
    // builds to: ~/.m2/repository/foo/bar/terraform/foo.bar.terraform.gradle.plugin/INSTALLER3-SNAPSHOT
    id("foo.bar.terraform") version "GET-AHEAD-OF-MYSELF-SNAPSHOT"
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


/*develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("no")
    }
}
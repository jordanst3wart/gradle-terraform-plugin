package org.ysb33r.gradle.terraform.config

interface VariableSpec {
    @Suppress("ktlint:standard:function-naming")
    fun `var`(
        name: String,
        value: Any,
    )

    fun map(
        name: String,
        map: Map<String, *>,
    )

    fun list(
        name: String,
        val1: Any,
        vararg vals: Any,
    )

    fun list(
        name: String,
        vals: Iterable<*>,
    )

    fun file(fileName: String)

    fun getCommandLineArgs(): List<String>
}
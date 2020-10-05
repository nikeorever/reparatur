@file:JvmName("Deps")

object Version {
    const val kotlin = "1.4.10"
}

object Dependencies {
    object Kotlin {
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Version.kotlin}"

        object Stdlib {
            const val common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Version.kotlin}"
            const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Version.kotlin}"
            const val jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlin}"
            const val jdk6 = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"
        }
    }

    object Android {
        const val gradlePlugin = "com.android.tools.build:gradle:4.0.1"
    }

    const val jarTransformer = "cn.nikeo.jar-transformer:jar-transformer:1.0.0"
    const val asm = "org.ow2.asm:asm:7.0"
}
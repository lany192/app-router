import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    // AGP 仅编译期需要，运行时由宿主工程提供
    compileOnly(libs.gradle)

    implementation(libs.commons.codec)
    implementation(libs.commons.io)
    implementation(libs.asm.util)
}

java {
    // AGP 9 / Gradle 9 要求 JDK 17+
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

gradlePlugin {
    plugins {
        create("ArouterPlugin") {
            id = "com.github.lany192.router"
            implementationClass = "com.alibaba.android.arouter.register.launch.PluginLaunch"
        }
    }
}

apply(from = rootProject.file("gradle/maven-publish.gradle.kts"))
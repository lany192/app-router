import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(project(":arouter-annotation"))
    implementation(libs.javapoet)
    implementation(libs.commons.lang3)
    implementation(libs.commons.collections4)
    implementation(libs.fastjson)

    // ksp deps https://github.com/google/ksp/releases/tag/1.7.20-1.0.6
    implementation(libs.ksp.symbol.processing.api)
    // https://square.github.io/kotlinpoet/
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.kotlinpoet.metadata)
}

apply(from = rootProject.file("gradle/maven-publish.gradle.kts"))
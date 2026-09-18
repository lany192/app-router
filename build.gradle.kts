buildscript {
    dependencies {
//        classpath(project(":arouter-register"))
        classpath(libs.lany.router.register)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    // AGP 9 内置 Kotlin 支持，kapt 需使用 com.android.legacy-kapt 插件
    alias(libs.plugins.android.legacy.kapt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}
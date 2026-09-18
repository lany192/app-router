import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.legacy.kapt)
    id("com.github.lany192.router")
}

val useOnlineLibrary = providers.gradleProperty("useOnlineLibrary").get().toBoolean()

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
        //是否debug模式
        arg("ROUTER_DEBUG", "true")
        //是否打印JS路由文档
        arg("ROUTER_JS_DOC", "true")
        //JS路由调用方法
        arg("ROUTER_JS_FUN", "window.app.route")
        //Uri Scheme标识
        arg("ROUTER_SCHEME", "appbox")
    }
}

android {
    namespace = "com.github.lany192.arouter.sample"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
        versionName = "0.0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    if (useOnlineLibrary) {
        implementation(libs.lany.router)
        kapt(libs.lany.router.compiler)
    } else {
        implementation(project(":arouter"))
        kapt(project(":arouter-compiler"))
    }

    implementation(project(":module:black"))
    implementation(project(":module:yellow"))
    implementation(project(":module:purple"))
    implementation(project(":module:common"))
    implementation(project(":module:blue"))
    implementation(project(":module:green"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit2)
    androidTestImplementation(libs.androidx.test.espresso)

    //超链接处理 https://github.com/klinker24/Android-TextView-LinkBuilder
    implementation(libs.link.builder)

    implementation(libs.gson)
    implementation(libs.toaster)
}
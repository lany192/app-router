import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.android.legacy.kapt)
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
    namespace = "com.github.lany192.purple"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit2)
    androidTestImplementation(libs.androidx.test.espresso)

    if (useOnlineLibrary) {
        implementation(libs.lany.router)
        kapt(libs.lany.router.compiler)
    } else {
        implementation(project(":arouter"))
        kapt(project(":arouter-compiler"))
    }
}
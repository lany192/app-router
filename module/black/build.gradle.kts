plugins {
    alias(libs.plugins.android.library)
}

val useOnlineLibrary = providers.gradleProperty("useOnlineLibrary").get().toBoolean()

android {
    namespace = "com.github.lany192.arouter.sample.service"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

        javaCompileOptions {
            annotationProcessorOptions {
                argument("AROUTER_MODULE_NAME", project.name)
            }
        }
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

dependencies {
    if (useOnlineLibrary) {
        implementation(libs.lany.router)
        annotationProcessor(libs.lany.router.compiler)
    } else {
        implementation(project(":arouter-annotation"))
        implementation(project(":arouter"))
        annotationProcessor(project(":arouter-compiler"))
    }

    implementation(libs.fastjson)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit2)
    androidTestImplementation(libs.androidx.test.espresso)
}
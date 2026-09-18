plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.alibaba.android.arouter"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

        javaCompileOptions {
            annotationProcessorOptions {
                argument("AROUTER_MODULE_NAME", project.name)
            }
        }
        consumerProguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
//            withJavadocJar()
        }
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    annotationProcessor(project(":arouter-compiler"))
    api(project(":arouter-annotation"))

    implementation(libs.androidx.appcompat)
}

apply(from = rootProject.file("gradle/maven-publish.gradle.kts"))
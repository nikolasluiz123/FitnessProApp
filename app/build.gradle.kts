plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotest)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.performance)
}

android {
    namespace = "br.com.fitnesspro"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.fitnesspro"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "br.com.fitnesspro.runner.FitnessProCustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes.addAll(
                mutableSetOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE.txt",
                    "META-INF/NOTICE.md",
                    "META-INF/LICENSE-notice.md",
                    "/META-INF/{AL2.0,LGPL2.1}"
                )
            )
        }
    }
}

dependencies {
    implementation(project(":fitnesspro-core"))
    implementation(project(":fitnesspro-compose-components"))
    implementation(project(":fitnesspro-model"))
    implementation(project(":fitnesspro-local-data-access"))

    implementation(project(":fitnesspro-common-module"))
    implementation(project(":fitnesspro-scheduler-module"))
    implementation(project(":fitnesspro-workout-module"))
    implementation(project(":fitnesspro-firebase-api"))

    implementation(libs.fitnesspro.shared.communication)

    implementation(libs.java.faker)
    implementation(libs.splash)
    implementation(libs.data.store)

    implementation(libs.google.gson)
    implementation(libs.google.credentials)
    implementation(libs.google.credentials.play.services)
    implementation(libs.google.id)

    implementation(libs.constraint.layout.compose)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.room.paging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.performance)
    implementation(libs.firebase.messaging)

    implementation(libs.work.runtime)

    debugImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
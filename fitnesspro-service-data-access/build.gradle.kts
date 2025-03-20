plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "br.com.fitnesspro.service.data.access"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        android.buildFeatures.buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("String", "FITNESS_PRO_SERVICE_HOST", "\"https://service.fitnessprotec.com/api/v1/\"")
        }

        debug {
            buildConfigField("String", "FITNESS_PRO_SERVICE_HOST", "\"https://service.fitnessprotec.com/api/v1/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes.addAll(
                mutableSetOf(
                    "META-INF/*spring*",
                    "META-INF/spring/*",
                    "META-INF/license.txt",
                    "META-INF/LICENSE.md",
                    "META-INF/NOTICE.md",
                    "META-INF/COPYRIGHT",
                    "META-INF/license.txt",
                    "META-INF/notice.txt"
                )
            )
        }
    }
}

dependencies {
    implementation(project(":fitnesspro-core"))
    implementation(project(":fitnesspro-model"))

    implementation(libs.fitnesspro.shared.communication)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.json)
    implementation(libs.service.logs)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso)
}
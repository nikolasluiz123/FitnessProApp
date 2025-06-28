import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotest)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

val appJWT: String = localProperties.getProperty("APP_JWT")

android {
    namespace = "br.com.fitnesspro.common"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        
        consumerProguardFiles("consumer-rules.pro")
        android.buildFeatures.buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "APP_JWT", "\"$appJWT\"")
        }

        debug {
            buildConfigField("String", "APP_JWT", "\"$appJWT\"")
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
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE.txt",
                    "META-INF/NOTICE.md",
                    "META-INF/LICENSE-notice.md",
                    "META-INF/INDEX.LIST"
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
    implementation(project(":fitnesspro-service-data-access"))
    implementation(project(":fitnesspro-firebase-api"))
    implementation(project(":fitnesspro-pdf-generator"))

    implementation(libs.fitnesspro.shared.communication)
    implementation(libs.data.store)

    implementation(libs.google.gson)
    implementation(libs.java.faker)
    implementation(libs.constraint.layout.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.work.runtime)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "br.com.fitnesspro.scheduler"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

tasks.withType<Test> {
    jvmArgs("--add-opens=java.base/java.time=ALL-UNNAMED")

    useJUnitPlatform()
}

dependencies {
    implementation(project(":fitnesspro-core"))
    implementation(project(":fitnesspro-compose-components"))
    implementation(project(":fitnesspro-model"))
    implementation(project(":fitnesspro-local-data-access"))
    implementation(project(":fitnesspro-service-data-access"))
    implementation(project(":fitnesspro-common-module"))
    implementation(project(":fitnesspro-firebase-api"))

    implementation(libs.fitnesspro.shared.communication)
    implementation(libs.data.store)

    implementation(libs.google.gson)
    implementation(libs.constraint.layout.compose)
    implementation(libs.java.faker)
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
    implementation(libs.firebase.analytics)

    testRuntimeOnly(libs.junit.jupter.engine)
    testImplementation(libs.junit.jupter.api)
    testImplementation(libs.kotest.assertion)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.ui.test.junit4)
    kspTest(libs.hilt.compiler)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockk.android)
    kspAndroidTest(libs.hilt.compiler)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
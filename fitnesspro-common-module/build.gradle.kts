plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotest)
}

android {
    namespace = "br.com.fitnesspro.common"
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
                    "META-INF/LICENSE-notice.md"
                )
            )
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":fitnesspro-core"))
    implementation(project(":fitnesspro-compose-components"))
    implementation(project(":fitnesspro-model"))
    implementation(project(":fitnesspro-local-data-access"))

    implementation(libs.google.gson)

    implementation(libs.java.faker)

    implementation(libs.constraint.layout.compose)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.room.paging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testRuntimeOnly(libs.junit.jupter.engine)
    testImplementation(libs.junit.jupter.api)
    testImplementation(libs.kotest.assertion)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.junit)
    kspTest(libs.hilt.compiler)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockk.android)
    kspAndroidTest(libs.hilt.compiler)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
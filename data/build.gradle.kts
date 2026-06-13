plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.codelegger.golfperformancetracker.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        // API base URL is configuration, not code. Override per environment with a Gradle
        // property — e.g. `-PGOLF_BASE_URL=...` or a line in ~/.gradle/gradle.properties —
        // and it flows through to BuildConfig.BASE_URL (consumed by NetworkModule).
        val baseUrl = (project.findProperty("GOLF_BASE_URL") as String?)
            ?: "https://6a2c5b9a3e2b60ab038fb5c0.mockapi.io/api/v1/"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

// Export Room schemas so migrations are reviewable and testable.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // Expose :domain to consumers (the app) — repositories return domain types.
    api(project(":domain"))

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging.interceptor)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // WorkManager + Hilt integration (the sync worker lives here)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Logging
    implementation(libs.timber)

    // Unit tests for parsing + mapping
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

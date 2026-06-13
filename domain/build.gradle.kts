plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.codelegger.golfperformancetracker.domain"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Domain is pure Kotlin + coroutines (Flow on repository interfaces). No Android UI,
    // no network, no persistence — those live in :data.
    implementation(libs.kotlinx.coroutines.core)
    // paging-common is pure Kotlin — lets the repository interface expose Flow<PagingData<T>>.
    api(libs.androidx.paging.common)
}

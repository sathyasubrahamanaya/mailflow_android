
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.flow.mailflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.flow.mailflow"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.compiler.processing.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.viewpager2)

    //    //sdp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //    //prefrance
    implementation(libs.androidx.preference.ktx)

    //Timber for logging
    implementation(libs.timber)

    ///Shimmer
    implementation (libs.shimmer)

    //networking
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.converter.scalars)
    implementation (libs.okhttp)
    implementation (libs.gson)
    implementation (libs.logging.interceptor)
    implementation (libs.dnsjava)

    //coroutines
    implementation (libs.kotlinx.coroutines.android)

    //lifecycle
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.lifecycle.extensions)
    implementation (libs.lottie)


}
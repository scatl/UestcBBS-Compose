plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    defaultConfig {
        applicationId = "com.scatl.uestcbbs.compose"
        namespace = "com.scatl.uestcbbs.compose"
        minSdk = libs.versions.minSdkVersion.get().toInt()
        compileSdk = libs.versions.compileSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    buildFeatures {
        compose = true
        buildConfig = true

        //解决安卓14 debug包卡顿
        prefab = true
    }
    packaging {
        resources {
            excludes.addAll(
                arrayOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "META-INF/LICENSE-LGPL-3.txt",
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE-W3C-TEST",
                    "META-INF/LICENSE-LGPL-2.1.txt",
                )
            )
        }
    }
}

dependencies {
    implementation(project(":hcaptcha"))
    implementation(project(":markdown"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.xlog)
    implementation(libs.lottie)
    implementation(libs.colorpicker)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.datastore)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.telephoto)
    implementation(libs.jsoup)
    implementation(libs.renderscript.intrinsics.replacement.toolkit)
    implementation(libs.richText)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.materialKolor)
    implementation(libs.compose.markdown)

    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.square)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.room)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.media3)
    implementation(libs.bundles.haze)

    ksp(libs.square.moshi.compiler)
    ksp(libs.google.hilt.compiler)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //解决安卓14 debug包卡顿
    implementation("com.bytedance.android:shadowhook:1.0.9")
    implementation("com.github.wuyouuuu:wytrace:1.0.1")
}
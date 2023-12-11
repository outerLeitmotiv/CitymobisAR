
plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "ch.hearc.ig.citymobis"
    compileSdk = 34

    defaultConfig {
        applicationId = "ch.hearc.ig.citymobis"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4" // Make sure this matches your Compose library version
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

    packaging {
        resources.pickFirsts.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation ("androidx.compose.ui:ui:1.5.4")
    implementation ("androidx.compose.material:material:1.5.4")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.esri.arcgisruntime:arcgis-android-toolkit:100.15.0")
    implementation ("com.esri:arcgis-maps-kotlin:200.3.0")
}
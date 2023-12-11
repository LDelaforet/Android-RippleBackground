plugins {
    // https://mvnrepository.com/artifact/com.android.library/com.android.library.gradle.plugin
    id("com.android.library") version "8.0.0"
    id("org.jetbrains.kotlin.android") version "1.9.0"
    id("maven-publish")
}

android {
    namespace = "com.ldelaforet.ripplebackground"
    compileSdk = 33

    defaultConfig {
        minSdk = 25
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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

    flavorDimensions("mode")
    productFlavors {
        create("full") {}
    }

    libraryVariants.all {
        // Assurez-vous que le composant 'fullRelease' est généré
        if (name == "fullRelease") {
            // Configurations spécifiques au composant 'fullRelease', le cas échéant
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.core:core-ktx:1.9.0")
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("fullRelease") {
                from(components["fullRelease"])

                groupId = "com.ldelaforet.rippleBackground"
                artifactId = "rippleBackground"
                version = "1.0"
            }
        }

        repositories {
            mavenLocal() // Utilisez mavenLocal pour publier localement
        }
    }
}
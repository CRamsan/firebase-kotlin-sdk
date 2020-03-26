/*
 * Copyright (c) 2020 GitLive Ltd.  Use of this source code is governed by the Apache 2.0 license.
 */


plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.3.70"
    `maven-publish`
}
repositories {
    mavenCentral()
    google()
}
version = "0.1.0-beta"

android {
    compileSdkVersion(property("targetSdkVersion") as Int)
    defaultConfig {
        minSdkVersion(property("minSdkVersion") as Int)
        targetSdkVersion(property("targetSdkVersion") as Int)
    }
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

kotlin {
    js {
        val main by compilations.getting {
            kotlinOptions {
                moduleKind = "commonjs"
            }
        }
    }
    android {
        publishLibraryVariants("release", "debug")
    }

    val iosArm64 = iosArm64()
    val iosX64 = iosX64("ios")

    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        val test by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>> {
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer"
        )
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.20.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("com.google.firebase:firebase-common:19.2.0")
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
            }
        }
        val jsMain by getting {
            dependencies {
//                implementation(npm("firebase", "6.2.3"))
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
            }
            kotlin.srcDir("src/androidMain/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
            kotlin.srcDir("src/androidTest/kotlin")
        }
        val iosMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.20.0")
            }
        }
        configure(listOf(iosArm64, iosX64)) {
            compilations.getByName("main") {
                source(sourceSets.get("iosMain"))
            }
        }

        cocoapods {
            summary = "Firebase Core for iOS (plus community support for macOS and tvOS)"
            homepage = "https://github.com/TeamHubApp/firebase-kotlin-multiplatform-sdk"
            //pod("FirebaseCore", "~> 6.3.1")
        }
    }
}

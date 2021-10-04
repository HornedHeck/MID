plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        nodejs {
        }
        binaries.executable()
    }
    jvm {
        withJava()
    }
    linuxX64{
        binaries{
            executable()
        }
    }
}
kotlin {
    /* Targets configuration omitted.
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    sourceSets {
        val commonMain by getting {
            dependencies {
                dependencies {
                    implementation(kotlin("stdlib-common"))
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
                    implementation("org.jetbrains.kotlin:kotlin-test-common")
                }
            }
            val jvmTest by getting{
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test-junit")
                }
            }
            val jsTest by getting{
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test-js")
                }
            }
        }
    }
}

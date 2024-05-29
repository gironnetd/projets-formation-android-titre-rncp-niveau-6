// Top-level build file where you can add configuration options common to all sub-projects/modules.
import io.gitlab.arturbosch.detekt.Detekt
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:${Versions.secretsGradlePlugin}")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationSafeArgsGradlePlugin}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.18.1")
}

detekt {
    toolVersion = "1.18.1"
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = true // activate all available (even unstable) rules.
    config = files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt

    source = files("$projectDir/app/src/main/java")

    reports {
        xml {
            xml.enabled = true
            xml.destination = file("$projectDir/build/detekt/report.xml")
        }
        html {
            html.enabled = true
            html.destination = file("$projectDir/build/detekt/report.html")
        }
        txt {
            txt.enabled = true
            txt.destination = file("$projectDir/build/detekt/report.txt")
        }
    }
}

// Kotlin DSL
tasks.withType<Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = "1.8"
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.18.1")
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    configurations.all {
        resolutionStrategy {
            //force 'asm:asm-all:3.3.1', 'commons-io:commons-io:1.4'
            force("org.objenesis:objenesis:2.6")
            force("androidx.test:monitor:1.4.0")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
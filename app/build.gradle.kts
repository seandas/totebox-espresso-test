import org.gradle.internal.os.OperatingSystem

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.seandas.totebox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.seandas.totebox"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // Allure's runner records each test; results go to TestStorage on the device (see allure.properties)
        testInstrumentationRunner = "io.qameta.allure.android.runners.AllureAndroidJUnitRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
        // Tests marked @FailureDemo fail on purpose: skipped normally, run alone with -PfailureDemo
        val failureDemo = "com.seandas.totebox.support.FailureDemo"
        if (project.hasProperty("failureDemo")) testInstrumentationRunnerArguments["annotation"] = failureDemo
        else testInstrumentationRunnerArguments["notAnnotation"] = failureDemo
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        animationsDisabled = true
    }
}

val allureVersion = "2.46.2"
val allureCommandline: Configuration by configurations.creating

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Lets Espresso wait for the app's simulated network calls (see util/AppIdling.kt)
    implementation("androidx.test.espresso:espresso-idling-resource:3.6.1")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // RecyclerView actions for the product and cart lists
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.test.services:storage:1.6.0")
    // Allure: steps, screenshots and logcat attached to each test
    androidTestImplementation("io.qameta.allure:allure-kotlin-model:2.4.0")
    androidTestImplementation("io.qameta.allure:allure-kotlin-commons:2.4.0")
    androidTestImplementation("io.qameta.allure:allure-kotlin-junit4:2.4.0")
    androidTestImplementation("io.qameta.allure:allure-kotlin-android:2.4.0")
    // Installed on the device so tests can write files that outlive the app (TestStorage)
    androidTestUtil("androidx.test.services:test-services:1.6.0")

    allureCommandline("io.qameta.allure:allure-commandline:$allureVersion@zip")
}

// ---------------------------------------------------------------------------------------------
// Allure report: after connectedDebugAndroidTest (pass or fail), collect the results that AGP
// pulled from the device and generate a single-file HTML report at <project>/allure-report.
// Both folders are git-ignored.
// ---------------------------------------------------------------------------------------------

val allureCliDir = layout.buildDirectory.dir("allure-commandline")
val allureResultsDir = rootProject.layout.projectDirectory.dir("allure-results")
val allureReportDir = rootProject.layout.projectDirectory.dir("allure-report")
// Where AGP puts files the tests wrote to TestStorage, one folder per device
val testStorageOutput = layout.buildDirectory.dir("outputs/connected_android_test_additional_output")

val unzipAllure = tasks.register<Sync>("unzipAllureCommandline") {
    // Lazy, so the download only happens when a report is generated
    from(allureCommandline.elements.map { files -> zipTree(files.single().asFile) })
    into(allureCliDir)
}

val collectAllureResults = tasks.register<Sync>("collectAllureResults") {
    description = "Copies the Allure results from the last connected test run into allure-results/."
    from(testStorageOutput) {
        include("**/allure-results/**")
        eachFile { path = name } // flatten: Allure files have unique names
        includeEmptyDirs = false
    }
    into(allureResultsDir)
    // Fills the report's Environment panel: app version and the device(s) the tests ran on
    val versionName = android.defaultConfig.versionName
    val deviceFolders = testStorageOutput.map { it.dir("debugAndroidTest/connected").asFile }
    doLast {
        val devices = deviceFolders.get().listFiles()?.filter { it.isDirectory }?.joinToString { it.name }
        allureResultsDir.file("environment.properties").asFile.writeText(
            "App=Totebox $versionName (debug)\nDevices=${devices ?: "unknown"}\n")
    }
    mustRunAfter(tasks.matching { it.name.startsWith("connected") && it.name.endsWith("AndroidTest") })
}

val allureReport = tasks.register<Exec>("allureReport") {
    group = "verification"
    description = "Generates allure-report/index.html from the last connected test run."
    dependsOn(unzipAllure, collectAllureResults)
    val bin = allureCliDir.get().dir("allure-$allureVersion/bin")
        .file(if (OperatingSystem.current().isWindows) "allure.bat" else "allure").asFile
    commandLine(bin.path, "generate", allureResultsDir.asFile.path,
        "-o", allureReportDir.asFile.path, "--clean", "--single-file")
    environment("JAVA_HOME", System.getProperty("java.home"))
    doLast { logger.lifecycle("Allure report: ${allureReportDir.file("index.html").asFile.toURI()}") }
}

tasks.matching { it.name == "connectedDebugAndroidTest" }.configureEach {
    finalizedBy(allureReport)
}

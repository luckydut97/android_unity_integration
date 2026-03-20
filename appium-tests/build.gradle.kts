plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    testImplementation("io.appium:java-client:9.2.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.seleniumhq.selenium:selenium-support:4.21.0")
}

tasks.test {
    useJUnitPlatform()
    dependsOn(":app:assembleDebug")

    val serverUrl = providers.environmentVariable("APPIUM_SERVER_URL")
        .orElse("http://127.0.0.1:4723")
    val deviceName = providers.environmentVariable("APPIUM_DEVICE_NAME")
        .orElse("Android Emulator")

    doFirst {
        systemProperty("APPIUM_SERVER_URL", serverUrl.get())
        systemProperty("APPIUM_DEVICE_NAME", deviceName.get())
        systemProperty(
            "APP_APK_PATH",
            rootProject.layout.projectDirectory.dir("app/build/outputs/apk/debug")
                .file("app-debug.apk")
                .asFile
                .absolutePath
        )
    }
}

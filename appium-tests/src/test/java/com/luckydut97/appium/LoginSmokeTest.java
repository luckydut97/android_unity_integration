package com.luckydut97.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginSmokeTest {

    private static AndroidDriver driver;
    private static final String APP_PACKAGE = "com.luckydut97.unity_integration_test";
    private static final String MAIN_ACTIVITY = "com.luckydut97.unity_integration_test.MainActivity";

    @BeforeAll
    static void setUpClass() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
            .setAutomationName("UiAutomator2")
            .setPlatformName("Android")
            .setDeviceName(getSystemProperty("APPIUM_DEVICE_NAME", "Android Emulator"))
            .setApp(resolveAppPath().toString())
            .setAppPackage(APP_PACKAGE)
            .setAppActivity(MAIN_ACTIVITY)
            .setAutoGrantPermissions(true);

        driver = new AndroidDriver(
            new URL(getSystemProperty("APPIUM_SERVER_URL", "http://127.0.0.1:4723")),
            options
        );
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void resetApp() {
        if (driver != null) {
            driver.terminateApp(APP_PACKAGE);
            driver.activateApp(APP_PACKAGE);
        }
    }

    @AfterAll
    static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void loginAndAddTodo() {
        fillField(AppiumBy.accessibilityId("usernameField"), "qa@appium.dev");
        fillField(AppiumBy.accessibilityId("passwordField"), "appium1234");
        waitForElement(AppiumBy.accessibilityId("loginButton")).click();

        String todoText = "Appium 스모크 체크";
        fillField(AppiumBy.accessibilityId("todoField"), todoText);
        waitForElement(AppiumBy.accessibilityId("addTodoButton")).click();

        Assertions.assertThat(
            driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().text(\"" + todoText + "\")"))
        )
            .as("입력한 할 일을 리스트에서 찾을 수 있어야 함")
            .isNotEmpty();
    }

    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void fillField(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.click();
        if (!trySetValueWithMobileCommand(element, text)) {
            element.clear();
            element.sendKeys(text);
        }
    }

    private boolean trySetValueWithMobileCommand(WebElement element, String text) {
        if (!(element instanceof RemoteWebElement remoteElement)) {
            return false;
        }
        try {
            driver.setClipboardText(text);
            driver.executeScript("mobile: paste", Map.of("elementId", remoteElement.getId()));
            return true;
        } catch (UnsupportedCommandException ignored) {
            return false;
        }
    }

    private static Path resolveAppPath() {
        String provided = getSystemProperty("APP_APK_PATH", null);
        if (provided != null) {
            Path candidate = Paths.get(provided);
            if (Files.exists(candidate)) {
                return candidate.toAbsolutePath();
            }
        }
        throw new IllegalStateException("APP_APK_PATH system property가 설정되지 않았거나 APK가 없습니다.");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}

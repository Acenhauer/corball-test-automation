package com.gromenaware.corball.appium;

import com.gromenaware.corball.drivers.GenericSauceDriver;
import com.gromenaware.corball.saucelabs.SauceHubParser;
import com.gromenaware.corball.saucelabs.SauceStorageUpload;
import com.gromenaware.corball.utils.PropertiesUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by guillemhs on 2015-11-29.
 */
public class MobileApp extends GenericSauceDriver {
    public AppiumDriver driver;
    public static final Properties testProperties =
            PropertiesUtils.getProcessedTestProperties();
    public static final String device = testProperties.getProperty(PropertiesUtils.DEVICE);
    public static final String platformVersion = testProperties.getProperty(PropertiesUtils.PLATFORMVERSION);
    public static final String appAbsolutePath =
            testProperties.getProperty(PropertiesUtils.APPABSOLUTEPATH);
    public static final String runningOnSauce =
            testProperties.getProperty(PropertiesUtils.SAUCE);

    public String getAppFolder(String appAbsolutePath) {
        Path currentRelativePath = Paths.get("");
        String currentPath = currentRelativePath.toAbsolutePath().toString();
        String[] parts = appAbsolutePath.split("/");
        String path = "";
        for (int i = 0; i <= parts.length - 2; i++) {
            path += File.separator + parts[i];
        }
        return currentPath + path + File.separator;
    }

    public String getAppFile(String appAbsolutePath) {
        String[] parts = appAbsolutePath.split("/");
        return parts[parts.length - 1];
    }

    @BeforeSuite
    public void setUp(Method method) throws IOException {
        // switch between different browsers, e.g. iOS Safari or Android Chrome
        // let's use the os name to differentiate, because we only use default browser in that os
        if (device != null && device.equalsIgnoreCase("Android")) {
            DesiredCapabilities caps = DesiredCapabilities.android();
            caps.setCapability("appiumVersion", "1.5.3");
            caps.setCapability("deviceName", "Android Emulator");
            caps.setCapability("deviceType", "phone");
            caps.setCapability("deviceOrientation", "portrait");
            caps.setCapability("browserName", "");
            caps.setCapability("platformVersion", platformVersion);
            caps.setCapability("platformName", "Android");
            if (runningOnSauce.equalsIgnoreCase("true")) {
                useSauceStorage();
                caps.setCapability("app", "sauce-storage:" + getAppFile(appAbsolutePath));
            } else {
                caps.setCapability("app", getAppFile(appAbsolutePath));
            }
            caps.setCapability("id", method.getName());
            caps.setCapability("name", method.getName());
            driver = new AndroidDriver(new URL(hub), caps);
            sessionId.set(driver.getSessionId().toString());
            globalAppiumDriver.set(driver);
            globalAppiumDriver.get().manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        } else {
            DesiredCapabilities caps = DesiredCapabilities.iphone();
            caps.setCapability("appiumVersion", "1.5.3");
            caps.setCapability("deviceName", "iPhone 6");
            caps.setCapability("deviceOrientation", "portrait");
            caps.setCapability("platformVersion", platformVersion);
            caps.setCapability("platformName", "iOS");
            caps.setCapability("browserName", "");
            if (runningOnSauce.equalsIgnoreCase("true")) {
                useSauceStorage();
                caps.setCapability("app", "sauce-storage:" + getAppFile(appAbsolutePath));
            } else {
                caps.setCapability("app", getAppFile(appAbsolutePath));
            }
            caps.setCapability("id", method.getName());
            caps.setCapability("name", method.getName());
            driver = new IOSDriver(new URL(hub), caps);
            sessionId.set(driver.getSessionId().toString());
            globalAppiumDriver.set(driver);
            globalAppiumDriver.get().manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        }
    }

    public void useSauceStorage() throws IOException {
        SauceStorageUpload sauceUploadFile = new SauceStorageUpload();
        sauceUploadFile.uploadFile(SauceHubParser.getUserSaucelabs(hub), SauceHubParser.getApikeySaucelabs(hub), appAbsolutePath);
    }

    @AfterSuite
    public void tearDown() {
        globalAppiumDriver.get().quit();
        globalAppiumDriver.get().close();
    }
}

package com.gromenaware.corball.drivers;

import com.gromenaware.corball.saucelabs.*;
import com.gromenaware.corball.selenium.BrowserCapabilities;
import com.gromenaware.corball.soap.SOAPClient;
import com.gromenaware.corball.utils.PropertiesUtils;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.core.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Listeners;

import java.util.Properties;

/**
 * Created by guillem on 15/02/16.
 */
@Listeners({SauceOnDemandTestListener.class})
public class GenericSauceDriver
        implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {
    public static final Properties testProperties =
            PropertiesUtils.getProcessedTestProperties();
    public static final String host = testProperties.getProperty(PropertiesUtils.HOST);
    public static final String hub = testProperties.getProperty(PropertiesUtils.HUB);
    public SauceOnDemandAuthentication authentication =
            new SauceOnDemandAuthentication(SauceHubParser.getUserSaucelabs(hub),
                    SauceHubParser.getApikeySaucelabs(hub));
    public InheritableThreadLocal<String> sessionId = new InheritableThreadLocal<>();
    public InheritableThreadLocal<RemoteWebDriver> globalDriver = new InheritableThreadLocal<>();
    public InheritableThreadLocal<AppiumDriver> globalAppiumDriver = new InheritableThreadLocal<>();
    public InheritableThreadLocal<Logger> globalLogger = new InheritableThreadLocal<Logger>();
    public InheritableThreadLocal<SOAPClient> globalXMLDriver =
            new InheritableThreadLocal<SOAPClient>();
    public InheritableThreadLocal<BrowserCapabilities> globalBrowserCapabilities =
            new InheritableThreadLocal<BrowserCapabilities>();

    protected RemoteWebDriver driver() {
        return globalDriver.get();
    }

    protected AppiumDriver appiumDriver() {
        return globalAppiumDriver.get();
    }

    protected Logger logger() {
        return globalLogger.get();
    }

    /**
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     * @return the host used for the test
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return authentication;
    }

    protected SOAPClient xmlDriver() {
        return globalXMLDriver.get();
    }
}

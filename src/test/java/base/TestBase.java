package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class TestBase {
    private ExtentReports reports;
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> parentTest = new ThreadLocal<>();
    public static final ThreadLocal<ExtentTest> testInfo = new ThreadLocal<>();
    private static final String USER = "user.dir";
    public static final String XPATH = "XPATH";
    public static final int TIME = 15;

    public static WebDriver getDriver() {
        return driver.get();
    }

    @AfterClass
    public void afterClass() {
        getDriver().quit();
    }

    public static String myUrl() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/data.json"));
        JSONObject envs = (JSONObject) config.get("url");
        String url = (String) envs.get("url");
        return System.getProperty("instance-url", url);
    }

    @BeforeSuite
    @Parameters({ "groupReport", "device" })
    public void setUp(String groupReport, String device) throws IOException, ParseException {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(new File(System.getProperty(USER) + groupReport));
        reports = new ExtentReports();
        reports.setSystemInfo("Test Environment", myUrl());
        if (device.equalsIgnoreCase("desktop")) {
            reports.setSystemInfo("Test Device", "Laptop");
        }
        reports.attachReporter(htmlReporter);
    }

    @Parameters({ "myBrowser", "server" })
    @BeforeClass
    public void beforeClass(String myBrowser, String server) throws IOException, ParseException {
        ExtentTest parent = reports.createTest(getClass().getName());
        parentTest.set(parent);

            // Local Directory
            if (myBrowser.equalsIgnoreCase("firefox")) {
                File classpathRoot = new File(System.getProperty(USER));
                File firefoxDriver = new File(classpathRoot, "geckodriver.exe");
                System.setProperty("webdriver.gecko.driver", firefoxDriver.getAbsolutePath());
                driver.set(new FirefoxDriver(OptionsManager.getFirefoxOptions()));
                getDriver().manage().window().maximize();
                getDriver().get(myUrl());

            } else if (myBrowser.equalsIgnoreCase("chrome")) {
                File classpathRoot = new File(System.getProperty(USER));
                File chromeDriver = new File(classpathRoot, "chromedriver.exe");
                System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
                driver.set(new ChromeDriver(OptionsManager.getChromeOptions()));
                getDriver().manage().window().maximize();
                getDriver().get(myUrl());
            }
        getDriver().get(myUrl());
    }

    @BeforeMethod(description = "fetch test cases name")
    public void register(Method method) {

        ExtentTest child = parentTest.get().createNode(method.getName());
        testInfo.set(child);
        testInfo.get().assignCategory("Regression");
        testInfo.get().getModel().setDescription(TestUtils.checkBrowser());
    }

    @AfterMethod(description = "to display the result after each test method")
    public void captureStatus(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE) {
            TestUtils.addScreenShot();
            testInfo.get().fail(result.getThrowable());
            getDriver().navigate().refresh();
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            TestUtils.addScreenShot();
            testInfo.get().pass(result.getName() + " Test passed");
        }
        else {
            TestUtils.addScreenShot();
            testInfo.get().skip(result.getThrowable());
        }
        reports.flush();
    }

    @AfterSuite(description = "Removes thread value, basically aids with memory management.")
    public void unload() {
        driver.remove();
        parentTest.remove();
    }
}

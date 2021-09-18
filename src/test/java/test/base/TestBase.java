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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class TestBase {
    public static String toAddress;
    public static String testMethod;
    public static ExtentReports reports;
    public static ExtentHtmlReporter htmlReporter;
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<>();
    public static ThreadLocal<ExtentTest> testInfo = new ThreadLocal<>();
    private static OptionsManager optionsManager = new OptionsManager();

    public static String htmlUrl;

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
    public void setUp(String groupReport, String device) throws Exception {
        htmlReporter = new ExtentHtmlReporter(new File(System.getProperty("user.dir") + groupReport));
        reports = new ExtentReports();
        reports.setSystemInfo("Test Environment", myUrl());
        if (device.equalsIgnoreCase("desktop")) {
            reports.setSystemInfo("Test Device", "Laptop");
        }

        reports.attachReporter(htmlReporter);
    }

    @Parameters({ "myBrowser", "environment", "server" })
    @BeforeClass
    public void beforeClass(String myBrowser, String environment, String server) throws Exception {
        ExtentTest parent = reports.createTest(getClass().getName());
        parentTest.set(parent);

//        if (server.equals("local")) {
            // Local Directory
            if (myBrowser.equalsIgnoreCase("firefox")) {
                File classpathRoot = new File(System.getProperty("user.dir"));
                File firefoxDriver = new File(classpathRoot, "geckodriver.exe");
                System.setProperty("webdriver.gecko.driver", firefoxDriver.getAbsolutePath());
                driver.set(new FirefoxDriver(optionsManager.getFirefoxOptions()));
                getDriver().manage().window().maximize();
//				getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
                getDriver().get(myUrl());

            } else if (myBrowser.equalsIgnoreCase("chrome")) {
                File classpathRoot = new File(System.getProperty("user.dir"));
                File chromeDriver = new File(classpathRoot, "chromedriver.exe");
                System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
                driver.set(new ChromeDriver(optionsManager.getChromeOptions()));
                getDriver().manage().window().maximize();
//				getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
                getDriver().get(myUrl());

            }
//        }

        getDriver().get(myUrl());
    }

    @BeforeMethod(description = "fetch test cases name")
    public void register(Method method) {

        ExtentTest child = parentTest.get().createNode(method.getName());
        testInfo.set(child);
        testInfo.get().assignCategory("Regression");
        testInfo.get().getModel().setDescription(TestUtils.CheckBrowser());
        testMethod = getClass().getName() + "-" + method.getName();
    }

    @AfterMethod(description = "to display the result after each test method")
    public void captureStatus(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = TestUtils.getScreenshot();
            testInfo.get().addScreenCaptureFromBase64String(screenshotPath);
            testInfo.get().fail(result.getThrowable());
            getDriver().navigate().refresh();
        } else if (result.getStatus() == ITestResult.SUCCESS)
            testInfo.get().pass(result.getName() + " Test passed");
        else
            testInfo.get().skip(result.getThrowable());

        reports.flush();
    }

    @Parameters({ "toMails", "groupReport" })
    @AfterSuite(description = "clean up report after test suite")
    public void cleanup(String toMails, String groupReport) {

        toAddress = toMails;
        SendMail.composeGmail("BioRegistra Portal Report <seamfix.test.report@gmail.com>", toAddress, groupReport);
    }
}

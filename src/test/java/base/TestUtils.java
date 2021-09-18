package base;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import enums.TargetTypeEnum;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestUtils extends TestBase{

    public static List<String> imgList = new ArrayList();

     @SuppressWarnings("resource")
    public static String getScreenshot() {
        TakesScreenshot ts = (TakesScreenshot) getDriver();
        File scrFile = ts.getScreenshotAs(OutputType.FILE);

        String encodedBase64 = null;
        FileInputStream fileInputStreamReader;
        try {
            fileInputStreamReader = new FileInputStream(scrFile);
            byte[] bytes = new byte[(int) scrFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encodeBase64(bytes));

        } catch (IOException e) {
            e.printStackTrace();
        }

         return "data:image/png;base64," + encodedBase64;
    }

    /*public static String takeScreenshot() throws IOException {
        String fileName = projectNameFolder + SUFFIX + folder + SUFFIX + testMethod + System.currentTimeMillis();
        TakesScreenshot ts = (TakesScreenshot) getDriver();
        byte[] pic = ts.getScreenshotAs(OutputType.BYTES);
        InputStream scrFile = new ByteArrayInputStream(pic);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(pic.length);
        meta.setContentType("image/png");

        String screenshot = createConnection().getUrl(bucketName, fileName).toExternalForm();
        imgList.add(screenshot);
        // Collections.sort(imgList);
        return screenshot;
    }*/

    public static boolean isAlertPresents() {
        try {
            getDriver().switchTo().alert();
            return true;
        } // try
        catch (Exception e) {
            return false;
        } // catch
    }

    public static String CheckBrowser() {
        // Get Browser name and version.
        Capabilities caps = ((RemoteWebDriver) getDriver()).getCapabilities();
        String browserName = caps.getBrowserName();
        String browserVersion = caps.getVersion();

        // return browser name and version.
        return browserName + " " + browserVersion;
    }

    /**
     * This method is used to clear input fields and send keys
     *
     */
    public static void sendKeys(By locator, String text) {
        getDriver().findElement(locator).clear();
        getDriver().findElement(locator).sendKeys(text);
    }

    /**
     * This method clicks on elements and also elements that are overlapped by other elements
     *
     */
    public static void clickElement(String type, String element) {
        JavascriptExecutor ex = (JavascriptExecutor) getDriver();
        WebElement clickThis = null;
        TargetTypeEnum targetTypeEnum = TargetTypeEnum.valueOf(type);
        clickThis = switch (targetTypeEnum) {
            case ID -> getDriver().findElement(By.id(element));
            case NAME -> getDriver().findElement(By.name(element));
            case CSSSELECTOR -> getDriver().findElement(By.cssSelector(element));
            case XPATH -> getDriver().findElement(By.xpath(element));
            case CLASSNAME -> getDriver().findElement(By.className(element));
        };
        ex.executeScript("arguments[0].click()", clickThis);
    }

    /**
     * @param type
     * @param element
     * @param value
     * @description to check if the expected text is present in the page.
     */
    public static void assertSearchText(String type, String element, String value) {

        StringBuilder verificationErrors = new StringBuilder();
        TargetTypeEnum targetTypeEnum = TargetTypeEnum.valueOf(type);
        String text = switch (targetTypeEnum) {
            case ID -> getDriver().findElement(By.id(element)).getText();
            case NAME -> getDriver().findElement(By.name(element)).getText();
            case CSSSELECTOR -> getDriver().findElement(By.cssSelector(element)).getText();
            case XPATH -> getDriver().findElement(By.xpath(element)).getText();
            case CLASSNAME -> getDriver().findElement(By.className(element)).getText();
        };

        try {
            Assert.assertEquals(text, value);
            testInfo.get().log(Status.INFO, value + " found");
        } catch (Error e) {
            verificationErrors.append(e.toString());
            String verificationErrorString = verificationErrors.toString();
            testInfo.get().error(value + " not found");
            testInfo.get().error(verificationErrorString);
        }
    }

    public static void header(String text) {
        Markup a = MarkupHelper.createLabel(text, ExtentColor.BLUE);
        testInfo.get().info(a);
    }

    public static void subHeader(String text) {
        Markup a = MarkupHelper.createLabel(text, ExtentColor.GREEN);
        testInfo.get().info(a);
    }
    public static void validationHeader(String text) {
        Markup a = MarkupHelper.createLabel(text, ExtentColor.BROWN);
        testInfo.get().info(a);
    }
}

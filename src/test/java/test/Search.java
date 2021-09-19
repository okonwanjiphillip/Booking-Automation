package test;

import base.TestBase;
import base.TestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Search extends TestBase {

    @Test
    @Parameters({"server"})
    public static void emptyLocationSearch() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        TestUtils.header("Search with empty location field");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ss")));
        TestUtils.closeModal();
        getDriver().findElement(By.id("ss")).clear();
        TestUtils.clickElement("XPATH", "//button[@type='submit']");
        TestUtils.assertSearchText("XPATH", "//div[@id='destination__error']/div", "Error: Please enter a destination to start searching.");
    }

    @Test
    @Parameters({"server"})
    public static void blankSpaceLocationSearch() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        TestUtils.header("Search with blank spaces location field");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ss")));
        TestUtils.sendKeys(By.id("ss"), "    ");
        TestUtils.clickElement("XPATH", "//button[@type='submit']"); //div[@id='right']/div[2]/div/div/div/h1
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='right']/div[2]/div/div/div/h1")));
        TestUtils.assertSearchText("XPATH", "//div[@id='right']/div[2]/div/div/div/h1", "0 properties are available in and around this destination");
    }

    /*@Test
    @Parameters({"server"})
    public static void specialCharacterLocationSearch() throws IOException, ParseException {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/data.json"));
        JSONObject envs = (JSONObject) config.get("searchChar");

        String specialChar = (String) envs.get("specialChar");

        TestUtils.header("Search with special characters in location field");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ss")));
        TestUtils.sendKeys(By.id("ss"), specialChar);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li")));

        // Assert the autocomplete feature
        List<WebElement> optionCount = getDriver().findElements(By.xpath("//div[2]/div/div/ul"));
        int recordCount = optionCount.size();
        System.out.println(recordCount);
        String flag = null;
        for (int i = 1; i <= recordCount; i++) {
            try {
                WebElement records = getDriver().findElement(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li["+ i +"]"));
                if (records != null) {
                    flag = getDriver().findElement(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li["+ i +"]/span[2]/span")).getText();
                    Assert.assertTrue(flag.contains(specialChar));
                }
                if (flag != null) {
                    boolean neededText = flag.contains(specialChar);
                    if (!neededText) {
                        testInfo.get().error(specialChar + " symbol is not present in " + flag);
                    } else {
                        testInfo.get().info(specialChar + " symbol is present in " + flag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TestUtils.clickElement("XPATH", "//button[@type='submit']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='hotellist_inner']/div")));
        TestUtils.assertSearchText("XPATH", "//div[@id='right']/div[2]/div/div/div/h1", "Nelspruit: 147 properties found");
        getDriver().navigate().refresh();
    }*/

    @Test
    @Parameters({"server"})
    public static void validLocationSearch() throws IOException, ParseException {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/data.json"));
        JSONObject envs = (JSONObject) config.get("searchChar");

        String validLocation = (String) envs.get("validLocation");

        TestUtils.header("Search for a valid location: " + validLocation);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ss")));
        TestUtils.sendKeys(By.id("ss"), validLocation);

        TestUtils.validationHeader("Check the auto-suggest feature of the location field.");

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li")));
        List<WebElement> optionCount = getDriver().findElements(By.xpath("//div[2]/div/div/ul"));
        int recordCount = optionCount.size();
        String flag = null;
        for (int i = 1; i <= recordCount; i++) {
            try {
                WebElement records = getDriver().findElement(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li["+ i +"]"));
                if (records != null) {
                    flag = getDriver().findElement(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li["+ i +"]/span[2]/span")).getText();
                    Assert.assertTrue(flag.contains(validLocation));
                }
                if (flag != null) {
                    boolean neededText = flag.contains(validLocation);
                    if (!neededText) {
                        testInfo.get().error(validLocation + " text is not present in " + flag);
                    } else {
                        testInfo.get().info(validLocation + " text is present in " + flag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TestUtils.clickElement("XPATH", "//button[@type='submit']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='right']/div[2]/div/div/div/h1")));
        TestUtils.assertSearchText("XPATH", "//div[@id='right']/div[2]/div/div/div/h1", "Limerick: 40 properties found");
    }

    @Test
    @Parameters({"server"})
    public static void setDate() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        TestUtils.header("Set Date to 3 months from current date");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.sb-date-field__display")));

        String newdate = TestUtils.getFutureDate(3);

        TestUtils.clickElement("XPATH", "//*[@id='frm']/div[3]/div/div[2]/div/div/div[2]");
        TestUtils.clickElement("XPATH", "//*[@id='frm']/div[3]/div/div[2]/div/div/div[2]");

        TestUtils.clickElement("XPATH", "//*[contains(@data-date,'"+newdate+"')]");

        TestUtils.assertSearchText("XPATH", "//div[@id='right']/div[2]/div/div/div/h1", "0 properties are available in and around this destination");
    }


}

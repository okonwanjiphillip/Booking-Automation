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
import java.util.logging.Logger;


public class Search extends TestBase {
    private static final String SEARCH_BUTTON = "//button[@type='submit']";
    private static final String LOCATION_FIELD = "ss";
    public static final String TEXT = "//div[@id='right']/div[2]/div/div/div/h1";

    @Test
    @Parameters({"server"})
    public static void emptyLocationSearch() {
        WebDriverWait wait = new WebDriverWait(getDriver(), TIME);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATION_FIELD)));
        TestUtils.closeModal();

        TestUtils.header("Search with empty location field");
        getDriver().findElement(By.id(LOCATION_FIELD)).clear();
        TestUtils.clickElement(XPATH, SEARCH_BUTTON);
        TestUtils.assertSearchText(XPATH, "//div[@id='destination__error']/div", "Error:\nPlease enter a destination to start searching.");
    }

    @Test
    @Parameters({"server"})
    public static void blankSpaceLocationSearch() throws IOException, ParseException {
        WebDriverWait wait = new WebDriverWait(getDriver(), TIME);

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/data.json"));
        JSONObject envs = (JSONObject) config.get("searchChar");

        String blankLocation = (String) envs.get("blankLocation");

        TestUtils.header("Search with blank spaces location field");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATION_FIELD)));
        TestUtils.sendKeys(By.id(LOCATION_FIELD), blankLocation);
        TestUtils.clickElement(XPATH, SEARCH_BUTTON); //div[@id='right']/div[2]/div/div/div/h1
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TEXT)));
        TestUtils.assertSearchText(XPATH, TEXT, "0 properties are available in and around this destination");
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
        WebDriverWait wait = new WebDriverWait(getDriver(), TIME);

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("resources/data.json"));
        JSONObject envs = (JSONObject) config.get("searchChar");

        String validLocation = (String) envs.get("validLocation");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(LOCATION_FIELD)));
        TestUtils.sendKeys(By.id(LOCATION_FIELD), validLocation);

        TestUtils.validationHeader("Check the auto-suggest feature of the location field.");

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li")));
        List<WebElement> optionCount = getDriver().findElements(By.xpath("//div[2]/div/div/ul"));
        int recordCount = optionCount.size();
        TestUtils.printList(recordCount, validLocation, By.xpath("//form[@id='frm']/div[2]/div/div/ul[1]/li[" + recordCount + "]/span[2]/span"));

        if (recordCount < 5) {
            testInfo.get().error("Error: Auto-suggestion list is supposed to have 5 items");
        }

        TestUtils.header("Search for a valid location: " + validLocation);

        TestUtils.clickElement(XPATH, SEARCH_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TEXT)));
        TestUtils.assertSearchText(XPATH, TEXT, "Limerick: 40 properties found");
    }

    @Test
    @Parameters({"server"})
    public static void setDateForSearch() {
        WebDriverWait wait = new WebDriverWait(getDriver(), TIME);

        TestUtils.header("Set Date to 3 months from current date");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.sb-date-field__display")));

        String newDate = TestUtils.getFutureDate(3);

        TestUtils.clickElement(XPATH, "//*[@id='frm']/div[3]/div/div[2]/div/div/div[2]");
        TestUtils.clickElement(XPATH, "//*[@id='frm']/div[3]/div/div[2]/div/div/div[2]");

        TestUtils.clickElement(XPATH, "//*[contains(@data-date,'" + newDate + "')]");
        TestUtils.clickElement(XPATH, SEARCH_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='hotellist_inner']/div[10]/div[2]/div/div/div/h3/a/span")));
        TestUtils.assertSearchText(XPATH, TEXT, "Limerick: 24 properties found");

//        TestUtils.listOfProperties();

        TestUtils.subHeader("Print List of first 10 Properties");

        for (int i = 1; i <= 10; i++) {
            String records = getDriver().findElement(By.xpath("//div[@id='hotellist_inner']/div[" +  i + "]/div[2]/div/div/div/h3/a/span")).getText();
            if (records != null) {
                testInfo.get().info(records);
            }
        }
    }

    @Test
    @Parameters({"server"})
    public static void filterByBudget() {
        WebDriverWait wait = new WebDriverWait(getDriver(), TIME);

        TestUtils.header("Filter by Budget");

        wait.until(ExpectedConditions.elementToBeClickable(By.id(LOCATION_FIELD)));

        TestUtils.scrollToElement(XPATH, "//div[@id='filter_price']/div[3]/a/label/div/span");

    }


}



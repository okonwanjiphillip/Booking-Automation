package test;

import base.TestBase;
import base.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class Search extends TestBase {

    @Test
    @Parameters({"server"})
    public static void emptyLocationSearch() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        TestUtils.header("Search with empty location field");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ss")));
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
        TestUtils.clickElement("XPATH", "//button[@type='submit']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='right']/div[2]/div/div/div/h1")));
        TestUtils.assertSearchText("XPATH", "//div[@id='right']/div[2]/div/div/div/h1", "0 properties are available in and around this destination");
    }
}

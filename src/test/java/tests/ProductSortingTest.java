package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductSortingTest {

    WebDriver driver;
    WebDriverWait wait;

    // Slow down for demo
    public void pause() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Type slowly, one letter at a time
    public void typeSlowly(By locator, String text) {
        WebElement field = driver.findElement(locator);
        for (char c : text.toCharArray()) {
            field.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    public void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.saucedemo.com/");
        pause();

        // Login
        typeSlowly(By.id("user-name"), "standard_user");
        pause();

        typeSlowly(By.id("password"), "secret_sauce");
        pause();

        driver.findElement(By.id("login-button")).click();

        // Navigate to inventory page
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        pause();
    }

    // Get all product names
    public List<String> getProductNames() {

        List<WebElement> productNames = driver.findElements(
                By.cssSelector("[data-test='inventory-item-name']")
        );

        List<String> actualNames = new ArrayList<>();

        for (WebElement product : productNames) {
            actualNames.add(product.getText());
        }

        return actualNames;
    }

    // Get all product prices
    public List<Double> getProductPrices() {

        List<WebElement> productPrices = driver.findElements(
                By.cssSelector("[data-test='inventory-item-price']")
        );

        List<Double> actualPrices = new ArrayList<>();

        for (WebElement price : productPrices) {
            actualPrices.add(Double.parseDouble(price.getText().replace("$", "")));
        }

        return actualPrices;
    }

    // Select sorting option
    public void selectSorting(String value) {

        WebElement sortElement = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-test='product-sort-container']")
                )
        );

        Select sortDropdown = new Select(sortElement);
        sortDropdown.selectByValue(value);
    }

    @Test
    public void verifyProductSorting() {

        // STEP 1: Apply Name (Z to A)
        selectSorting("za");
        pause();

        List<String> actualZA = getProductNames();

        List<String> expectedZA = new ArrayList<>(actualZA);
        expectedZA.sort(Collections.reverseOrder());

        assertEquals(expectedZA, actualZA,
                "Products are not sorted by Name Z to A");

        // STEP 2: Apply Price (Low to High)
        selectSorting("lohi");
        pause();

        List<Double> actualLowHigh = getProductPrices();

        List<Double> expectedLowHigh = new ArrayList<>(actualLowHigh);
        Collections.sort(expectedLowHigh);

        assertEquals(expectedLowHigh, actualLowHigh,
                "Products are not sorted by Price Low to High");

        pause();

        System.out.println("Name Z to A and Price Low to High sorting verified!");
    }

    @AfterEach
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
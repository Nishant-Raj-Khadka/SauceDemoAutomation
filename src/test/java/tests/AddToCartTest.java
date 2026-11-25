package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddToCartTest {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    @BeforeEach
    public void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);

        driver.get("https://www.saucedemo.com/");

        // Login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Navigate to inventory page
        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }

    // Slow down for demo
    public void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Hover over item and click Add to Cart
    public void addItemToCart(String itemId) {

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-test='add-to-cart-" + itemId + "']")
                )
        );

        actions.moveToElement(addButton).perform();
        pause();

        addButton.click();
        pause();
    }

    @Test
    public void verifyAddToCart() {

        pause();

        // Item names we expect to see in the cart
        List<String> expectedItems = new ArrayList<>();
        expectedItems.add("Sauce Labs Backpack");
        expectedItems.add("Sauce Labs Bike Light");
        expectedItems.add("Sauce Labs Bolt T-Shirt");

        // STEP 1: Add three items (hover + click)
        addItemToCart("sauce-labs-backpack");
        addItemToCart("sauce-labs-bike-light");
        addItemToCart("sauce-labs-bolt-t-shirt");

        // STEP 2: Cart badge should show 3
        String badgeCount = driver.findElement(
                By.cssSelector("[data-test='shopping-cart-badge']")
        ).getText();

        assertEquals("3", badgeCount, "Cart badge count is not 3");

        // STEP 3: Open cart
        driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
        wait.until(ExpectedConditions.urlContains("cart.html"));
        pause();

        // STEP 4: Verify items inside the cart
        List<WebElement> cartItems = driver.findElements(
                By.cssSelector("[data-test='inventory-item-name']")
        );

        List<String> actualItems = new ArrayList<>();
        for (WebElement item : cartItems) {
            actualItems.add(item.getText());
        }

        assertEquals(expectedItems.size(), actualItems.size(),
                "Number of items in cart is not correct");

        for (String expected : expectedItems) {
            assertTrue(actualItems.contains(expected),
                    expected + " was not found in the cart");
        }

        pause();

        System.out.println("Items added to cart successfully: " + actualItems);
    }

    @AfterEach
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
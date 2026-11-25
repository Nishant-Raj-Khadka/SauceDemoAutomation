package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckoutTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.saucedemo.com/");

        // Login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        wait.until(ExpectedConditions.urlContains("inventory.html"));

        // Add items to cart (needed before checkout)
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-bike-light']")).click();
    }

    // Slow down for demo
    public void pause() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyCheckout() {

        // STEP 1: Navigate to the cart page
        driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
        wait.until(ExpectedConditions.urlContains("cart.html"));
        pause();

        // STEP 2: Proceed to checkout
        driver.findElement(By.cssSelector("[data-test='checkout']")).click();
        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        pause();

        // STEP 3: Fill in billing and shipping information
        driver.findElement(By.cssSelector("[data-test='firstName']")).sendKeys("Nishant");
        driver.findElement(By.cssSelector("[data-test='lastName']")).sendKeys("Khadka");
        driver.findElement(By.cssSelector("[data-test='postalCode']")).sendKeys("44600");
        pause();

        driver.findElement(By.cssSelector("[data-test='continue']")).click();
        wait.until(ExpectedConditions.urlContains("checkout-step-two.html"));
        pause();

        // STEP 4: Complete the checkout (Finish)
        driver.findElement(By.cssSelector("[data-test='finish']")).click();
        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));

        // STEP 5: Verify order confirmation
        WebElement confirmation = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='complete-header']")
                )
        );

        assertEquals("Thank you for your order!", confirmation.getText(),
                "Order confirmation message is not correct");

        assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"),
                "User is not on the checkout complete page");

        pause();

        System.out.println("Checkout successful: " + confirmation.getText());
    }

    @AfterEach
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
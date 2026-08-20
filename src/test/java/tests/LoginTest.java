package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com/login";

    @BeforeMethod
    public void setUp() {
        // On your local Mac, Selenium finds Chrome automatically - no env var needed.
        // Inside the Jenkins container, we installed Chromium at a non-default path,
        // so we point BOTH WebDriverManager AND ChromeOptions at it - WebDriverManager
        // needs this too, otherwise it downloads a chromedriver version that doesn't
        // match our actual browser, which is what was crashing before.
        String chromeBinary = System.getenv("CHROME_BIN");

        WebDriverManager wdm = WebDriverManager.chromedriver();
        if (chromeBinary != null && !chromeBinary.isEmpty()) {
            wdm.browserPath(chromeBinary);
        }
        wdm.setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        if (chromeBinary != null && !chromeBinary.isEmpty()) {
            options.setBinary(chromeBinary);
        }

        driver = new ChromeDriver(options);
    }

    @Test
    public void validLoginShouldSucceed() {
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String flashMessage = driver.findElement(By.id("flash")).getText();
        Assert.assertTrue(flashMessage.contains("You logged into a secure area"),
                "Expected success message not found. Actual: " + flashMessage);
    }

    @Test
    public void invalidLoginShouldFail() {
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("wronguser");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String flashMessage = driver.findElement(By.id("flash")).getText();
        Assert.assertTrue(flashMessage.contains("Your username is invalid"),
                "Expected failure message not found. Actual: " + flashMessage);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

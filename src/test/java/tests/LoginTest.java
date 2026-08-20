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
        // WebDriverManager auto-downloads the matching chromedriver version.
        // This matters a lot in Jenkins/Docker, where there's no chromedriver
        // pre-installed on the machine.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Headless mode is mandatory when running inside the Jenkins container -
        // there is no display/monitor available for a real browser window.
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

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

package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private WebDriver driver;

    private static String resolveBaseUrl(String env) {
        return switch (env) {
            case "staging" -> "https://the-internet.herokuapp.com/login";
            case "prod" -> "https://the-internet.herokuapp.com/login";
            default -> "https://the-internet.herokuapp.com/login"; // qa (default)
        };
    }

    @BeforeMethod
    public void setUp() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        String env = System.getProperty("env", "qa").toLowerCase();
        System.out.println(">>> Running against browser=" + browser + " env=" + env);

        String chromeBinary = System.getenv("CHROME_BIN");
        String chromedriverBinary = System.getenv("CHROMEDRIVER_BIN");
        String firefoxBinary = System.getenv("FIREFOX_BIN");

        if ("firefox".equals(browser)) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            if (firefoxBinary != null && !firefoxBinary.isEmpty()) {
                options.setBinary(firefoxBinary);
            }
            driver = new FirefoxDriver(options);
        } else {
            if (chromedriverBinary != null && !chromedriverBinary.isEmpty()) {
                System.setProperty("webdriver.chrome.driver", chromedriverBinary);
            } else {
                WebDriverManager.chromedriver().setup();
            }

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
    }

    @Test
    public void validLoginShouldSucceed() {
        String baseUrl = resolveBaseUrl(System.getProperty("env", "qa").toLowerCase());
        driver.get(baseUrl);
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String flashMessage = driver.findElement(By.id("flash")).getText();
        Assert.assertTrue(flashMessage.contains("You logged into a secure area"),
                "Expected success message not found. Actual: " + flashMessage);
    }

    @Test
    public void invalidLoginShouldFail() {
        String baseUrl = resolveBaseUrl(System.getProperty("env", "qa").toLowerCase());
        driver.get(baseUrl);
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

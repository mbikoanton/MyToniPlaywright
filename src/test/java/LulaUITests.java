import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LulaUITests {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private String baseURL = "https://lula.co.za";
    private String aboutYourselfUrl = "https://secure.lula.co.za/about-yourself";
    private String password = "p@ssw0rD";
    private String randomEmail = utils.EmailGenerator.generateRandomEmail();
    private APIRequestContext apiRequestContext;

    @BeforeClass
    public void setup() {
        // Set up Playwright and launch the browser
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // Create a new browser context with no viewport size to simulate maximized window
        BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));

        // Open a new page
        page = context.newPage();

        // Navigate to Lula's website
        page.navigate(baseURL);

        // Clear cookies window
        clearCookies(page);

        // Set up the API context
        apiRequestContext = playwright.request().newContext();
    }

    @Test
    public void testBusinessAccountSignupFlow() {
        // Click 'Get Started' and fill the form with email and password
        //page.click("text='Get Started'");
        page.locator("//*[@id='home-hero']/div/div[1]/div[1]/div/a").click();
        page.locator("//*[@id='root']/div/div/section/div/div[2]/section/form/div[1]/div[2]/input").fill(randomEmail);
        page.locator("//*[@id='root']/div/div/section/div/div[2]/section/form/div[2]/div[2]/input").fill(password);
        page.locator("//*[@id='root']/div/div/section/div/div[2]/section/form/button").click();

        // Wait for the new page to load and verify the URL
        page.waitForTimeout(10000);
        Assert.assertEquals(page.url(), aboutYourselfUrl, "URL does not match the expected value!");
    }

    @Test
    public void testAPIProductDetails() {
        // API Call to Open Pet Food Facts API
        APIResponse response = apiRequestContext.get("https://world.openpetfoodfacts.org/api/v0/product/20106836.json");

        // Parse JSON response
        String responseBody = response.text();
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

        // Validate a nested value
        String thumbUrl = jsonObject
                .getAsJsonObject("product")
                .getAsJsonObject("selected_images")
                .getAsJsonObject("front")
                .getAsJsonObject("thumb")
                .get("fr")
                .getAsString();

        Assert.assertEquals(thumbUrl, "https://images.openpetfoodfacts.org/images/products/20106836/front_fr.5.100.jpg", "thumb Url does not match!");
    }

    @AfterClass
    public void clearBrowser() {
        // Close the API request context first
        if (apiRequestContext != null) {
            apiRequestContext.dispose();
        }

        // Then close the browser
        if (browser != null) {
            browser.close();
        }

        // Finally, dispose of Playwright
        if (playwright != null) {
            playwright.close();
        }
    }


    public void clearCookies(Page page) {
        // Handle the cookie consent popup by clicking the "Accept All" button
        if (page.isVisible("text='Accept All'")) {
            page.click("text='Accept All'");
        }
    }
}

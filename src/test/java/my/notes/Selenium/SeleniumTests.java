package my.notes.Selenium;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.transaction.Transactional;
import my.notes.notesApp.NotesJavaApp;
import my.notes.notesApp.biz.service.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTests {

    public static final int RANDOM_INT = (int) (Math.random() * 100 + 1);

    public void deleteSpecificNote(String noteTitle) {
        List<WebElement> notes = driver.findElements(By.cssSelector("#notesList .card"));
        for (WebElement note : notes) {
            WebElement titleElement = note.findElement(By.id("eachNoteTitle"));
            if (titleElement.getText().equals(noteTitle)) {
                WebElement deleteButton = note.findElement(By.xpath(".//a[contains(text(),'Delete')]"));
                deleteButton.click();
                return;
            }
        }
        throw new NoSuchElementException("Note with title '" + noteTitle + "' not found.");
    }

    public void editSpecificNote(String noteTitle, String newContent) {
        List<WebElement> notes = driver.findElements(By.cssSelector("#notesList .card"));
        for (WebElement note : notes) {
            WebElement titleElement = note.findElement(By.id("eachNoteTitle"));
            if (titleElement.getText().equals(noteTitle)) {
                WebElement editButton = note.findElement(By.xpath(".//a[contains(text(),'Edit')]"));

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", editButton);
                editButton.click();

                WebElement contentField = driver.findElement(By.id("content"));
                contentField.clear();
                contentField.sendKeys(newContent);
                driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
                return;
            }
        }
    }

    WebDriver driver;
    Dotenv dotenv = Dotenv.load();

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }

    @Test
    public void loginAsAdmin() {
        driver.get("http://localhost:8080/login");
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(d -> loginForm.isDisplayed());

        WebElement userNameField = driver.findElement(By.name("username"));
        WebElement userPassword = driver.findElement(By.name("password"));
        userNameField.sendKeys(dotenv.get("ADMIN_USERNAME"));
        userPassword.sendKeys(dotenv.get("ADMIN_PASSWORD"));

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        WebElement message = driver.findElement(By.id("myNotesHeader"));
        String value = message.getText();

        assertEquals("My Notes", value, "The page with notes should be accessible by logged in user.");
        assertTrue(message.isDisplayed(), "The page with notes should be displayed.");
    }

    @Test
    public void createDeleteAndEditNoteAsAdmin() {
        driver.get("http://localhost:8080/login");
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(d -> loginForm.isDisplayed());

        WebElement userNameField = driver.findElement(By.name("username"));
        WebElement userPassword = driver.findElement(By.name("password"));
        userNameField.sendKeys(dotenv.get("ADMIN_USERNAME"));
        userPassword.sendKeys(dotenv.get("ADMIN_PASSWORD"));

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        // create a note
        WebElement createNoteButton = driver.findElement(By.id("createNoteButton"));
        wait.until(d -> createNoteButton.isDisplayed());
        createNoteButton.click();

        WebElement titleField = driver.findElement(By.name("title"));
        WebElement contentField = driver.findElement(By.name("content"));
        wait.until(d -> titleField.isDisplayed());
        titleField.sendKeys("TestNoteCreatedByTests" + RANDOM_INT);
        contentField.sendKeys("TestNoteContentCreatedByTests" + RANDOM_INT);

        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();

        WebElement createdNoteTitle = driver.findElement(By.id("eachNoteTitle"));
        wait.until(d -> createdNoteTitle.isDisplayed());

        assertTrue(driver.findElements(By.id("notesList")).stream().anyMatch(note -> note.getText().contains("TestNoteCreatedByTests" + RANDOM_INT)),
                "The newly created note should appear in the list.");

        // edit the note
        wait.until(d -> createdNoteTitle.isDisplayed());
        editSpecificNote("TestNoteCreatedByTests" + RANDOM_INT, "testNewContentForTheNote - This is my new content, generated by tests");
        assertTrue(driver.findElements(By.id("notesList")).stream().anyMatch(note -> note.getText().contains("testNewContentForTheNote - This is my new content, generated by tests")),
                "The edited note should contain updated content.");

        // delete the note
        deleteSpecificNote("TestNoteCreatedByTests" + RANDOM_INT); // no need for assertions sine returns an error if failed
    }

    @Test
    public void loginAsUser() {
        driver.get("http://localhost:8080/login");
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(d -> loginForm.isDisplayed());

        WebElement userNameField = driver.findElement(By.name("username"));
        WebElement userPassword = driver.findElement(By.name("password"));
        userNameField.sendKeys(dotenv.get("USER_USERNAME"));
        userPassword.sendKeys(dotenv.get("USER_PASSWORD"));

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        WebElement message = driver.findElement(By.id("myNotesHeader"));
        String value = message.getText();
        assertEquals("My Notes", value, "The page with notes should be accessible by logged in user");
        assertTrue(message.isDisplayed(), "The page with notes should be displayed.");
    }

    @Test
    public void createDeleteAndEditNoteAsUser() {
        driver.get("http://localhost:8080/login");
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(d -> loginForm.isDisplayed());

        WebElement userNameField = driver.findElement(By.name("username"));
        WebElement userPassword = driver.findElement(By.name("password"));
        userNameField.sendKeys(dotenv.get("USER_USERNAME"));
        userPassword.sendKeys(dotenv.get("USER_PASSWORD"));

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        // create a note
        WebElement createNoteButton = driver.findElement(By.id("createNoteButton"));
        wait.until(d -> createNoteButton.isDisplayed());
        createNoteButton.click();

        WebElement titleField = driver.findElement(By.name("title"));
        WebElement contentField = driver.findElement(By.name("content"));
        wait.until(d -> titleField.isDisplayed());
        titleField.sendKeys("TestNoteCreatedByTests" + RANDOM_INT);
        contentField.sendKeys("TestNoteContentCreatedByTests" + RANDOM_INT);

        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();

        WebElement createdNoteTitle = driver.findElement(By.id("eachNoteTitle"));
        wait.until(d -> createdNoteTitle.isDisplayed());

        assertTrue(driver.findElements(By.id("notesList")).stream().anyMatch(note -> note.getText().contains("TestNoteCreatedByTests" + RANDOM_INT)),
                "The newly created note should appear in the list.");

        // edit the note
        editSpecificNote("TestNoteCreatedByTests" + RANDOM_INT, "testNewContentForTheNote - This is my new content, generated by tests");
        assertTrue(driver.findElements(By.id("notesList")).stream().anyMatch(note -> note.getText().contains("testNewContentForTheNote - This is my new content, generated by tests")),
                "The edited note should contain updated content.");

        // delete the note
        deleteSpecificNote("TestNoteCreatedByTests" + RANDOM_INT); // no need for assertions sine returns an error if failed
    }

    @Test
    public void registerAndLoginAsANewUser() {
        driver.get("http://localhost:8080/login");
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(d -> loginForm.isDisplayed());

        WebElement signUpButton = driver.findElement(By.id("signUpButton"));
        signUpButton.click();

        WebElement userNameField = driver.findElement(By.name("username"));
        WebElement userEmailField = driver.findElement(By.name("email"));
        WebElement userPassword = driver.findElement(By.name("password"));
        userNameField.sendKeys("ths_is_a_new_user");
        userEmailField.sendKeys("ths_is_a_new_user_email@mail.com");
        userPassword.sendKeys("ths_is_a_new_user_password_123987");

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        WebElement userNameField2 = driver.findElement(By.name("username"));
        WebElement userPassword2 = driver.findElement(By.name("password"));
        userNameField2.sendKeys("ths_is_a_new_user");
        userPassword2.sendKeys("ths_is_a_new_user_password_123987");

        WebElement loginButton = driver.findElement(By.cssSelector("button"));
        loginButton.click();

        WebElement message = driver.findElement(By.id("myNotesHeader"));
        String value = message.getText();
        assertEquals("My Notes", value, "The page with notes should be accessible by logged in user");
        assertTrue(message.isDisplayed(), "The page with notes should be displayed.");
    }

    @Test
    public void deleteUserAsAdmin() {
        driver.get("http://localhost:8080/login");
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(d -> loginForm.isDisplayed());

        WebElement userNameField = driver.findElement(By.name("username"));
        WebElement userPassword = driver.findElement(By.name("password"));
        userNameField.sendKeys(dotenv.get("ADMIN_USERNAME"));
        userPassword.sendKeys(dotenv.get("ADMIN_PASSWORD"));

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        WebElement manageUsersButton = driver.findElement(By.id("manageUsersButton"));
        manageUsersButton.click();

        WebElement deleteButton = driver.findElement(By.xpath("//h5[text()='copper0334']/following-sibling::p/following-sibling::form/button"));
        deleteButton.click();

        boolean isUserDeleted = driver.getPageSource().contains(dotenv.get("TEST_USER_USERNAME"));
        assertFalse(isUserDeleted, "The user should have been deleted.");
    }
}
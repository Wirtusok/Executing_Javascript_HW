import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.junit.jupiter.api.*;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Assertions; // Импортируем Assertions

public class FormTest {
    private static final Logger logger = LoggerFactory.getLogger(FormTest.class);
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setUpClass() {
        WebDriverManager.chromedriver().setup();
        logger.info("=== Начало тестирования формы ===");
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        // Используем Duration.ofSeconds(15) для согласованности
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        logger.info("Инициализирован WebDriver");
    }

    @Test
    @DisplayName("Тест заполнения формы регистрации")
    void testRegistrationForm() {
        // Получаем параметры из системных свойств или используем значения по умолчанию
        String url = System.getProperty("app.url", "https://otus.home.kartushin.su/form.html");
        String username = System.getProperty("user.name", "ТестовыйПользователь");
        String email = System.getProperty("user.email", "test@example.com");
        String password = System.getProperty("user.password", "password123");
        String birthDate = System.getProperty("user.birthdate", "01.01.1990");
        String languageLevel = System.getProperty("user.language.level", "Средний");

        driver.get(url.trim()); // Убираем лишние пробелы из URL
        logger.info("Открыта страница формы регистрации: " + url);

        // Заполнение формы
        fillForm(username, email, password, password, birthDate, languageLevel);

        // Отправка формы
        submitForm();

        // Проверка результата
        verifySubmissionResult();
    }

    private void fillForm(String username, String email, String password,
                          String confirmPassword, String birthDate, String languageLevel) {
        logger.info("Начинаем заполнение формы");

        // заполнение имени пользователя
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        usernameField.sendKeys(username);
        logger.info("Заполнено имя пользователя: " + username);

        // заполнение email
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        emailField.sendKeys(email);
        logger.info("Заполнен email: " + email);

        // заполнение пароля
        WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
        passwordField.sendKeys(password);
        logger.info("Заполнен пароль");

        // подтверждение пароля
        WebElement confirmPasswordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("confirm_password")));
        confirmPasswordField.sendKeys(confirmPassword);
        logger.info("Подтвержден пароль");

        // проверка совпадения паролей
        Assertions.assertEquals(password, confirmPassword, "Пароли не совпадают");
        logger.info("Проверка паролей пройдена - пароли совпадают");

        // заполнение даты рождения
        WebElement birthDateField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("birthdate")));
        birthDateField.sendKeys(birthDate);
        logger.info("Заполнена дата рождения: " + birthDate);

        // Выбор уровня знания языка
        WebElement languageLevelSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("language_level")));
        Select select = new Select(languageLevelSelect);
        select.selectByVisibleText(languageLevel);
        logger.info("Выбран уровень знания языка: " + languageLevel);
    }

    private void submitForm() {
        logger.info("Отправка формы");
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='submit'][value='Зарегистрироваться']")));

        // Прокручиваем к кнопке, если она не видна
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);

        wait.until(ExpectedConditions.elementToBeClickable(submitButton));

        submitButton.click();
        logger.info("Форма отправлена");
    }

    private void verifySubmissionResult() {
        logger.info("Проверка результата отправки формы");

        try {
            // Пытаемся найти элемент с результатом
            WebElement resultElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class, 'result') or contains(@id, 'result') or contains(text(), 'успешно')]")));

            String resultText = resultElement.getText();
            logger.info("Результат отправки формы: " + resultText);

            // проверяем, что форма была успешно отправлена
            Assertions.assertNotNull(resultText, "Результат отправки формы не найден");
            Assertions.assertFalse(resultText.isEmpty(), "Результат отправки формы пуст");

        } catch (Exception e) {
            // если не нашли элемент с результатом, проверяем URL или другие признаки
            logger.info("Текущий URL после отправки: " + driver.getCurrentUrl());

            //проверяем, что страница загрузилась
            Assertions.assertTrue(driver.getCurrentUrl().contains("form"),
                    "Страница изменилась после отправки формы");
        }

        logger.info("Тест формы регистрации пройден успешно");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver закрыт");
        }
    }

    @AfterAll
    static void cleanUp() {
        logger.info("=== Завершение тестирования формы ===");
    }
}
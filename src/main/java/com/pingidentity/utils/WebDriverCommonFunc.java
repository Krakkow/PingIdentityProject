package com.pingidentity.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.pingidentity.POJOs.NewsItem;

public class WebDriverCommonFunc {

    private WebDriver driver;
    private WebDriverWait wait;

    public WebDriverCommonFunc(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void goToPage(String url) {
        if (url != null && !url.isEmpty()) {
            driver.get(url);
        } else {
            throw new IllegalArgumentException("URL must not be null or empty");
        }
    }

    public WebElement waitUntilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
    }

    public WebElement findElement(By locator) {
        return driver.findElement(locator);
    }

    public boolean doesElementExist(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    public void click(By locator) {
        waitUntilVisible(locator).click();
    }

    public void type(By locator, String text) {
        WebElement element = waitUntilVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void typeAndSubmit(By locator, String text) {
        WebElement element = waitUntilVisible(locator);
        element.clear();
        element.sendKeys(text);
        element.submit();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getTextFromElement(By locator) {
        return waitUntilVisible(locator).getText();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public List<NewsItem> getArticlesByType(
            By articleBlockLocator,
            By titleLocator,
            By dateLocator,
            int maxCount,
            String expectedType,
            String origin,
            DateTimeFormatter formatter) {
        List<WebElement> articleBlocks = findElements(articleBlockLocator);
        List<NewsItem> newsList = new ArrayList<>();

        int count = 0;
        for (WebElement article : articleBlocks) {
            if (count >= maxCount)
                break;

            NewsItem item = extractNewsItem(article, titleLocator, dateLocator, expectedType, origin, formatter);
            if (item != null) {
                newsList.add(item);
                count++;
            }
        }

        return newsList;
    }

    public NewsItem extractNewsItem(
            WebElement article,
            By titleLocator,
            By dateLocator,
            String type,
            String origin,
            DateTimeFormatter formatter) {
        try {
            WebElement anchor = article.findElement(titleLocator);
            WebElement dateElement = article.findElement(dateLocator);

            String title = anchor.getAttribute("aria-label");
            String url = anchor.getAttribute("href").trim();
            String rawDate = dateElement.getText().trim();
            LocalDate publishedDate = LocalDate.parse(rawDate, formatter);

            return new NewsItem(origin, publishedDate, type, title, url);

        } catch (Exception e) {
            System.err.println("Error extracting news item: " + e.getMessage());
            return null;
        }
    }

    public void filterByDropdownOption(By dropdownLocator, By optionLocators, By selectedLocator,
            String optionToSelect) {
        click(dropdownLocator);

        List<WebElement> options = findElements(optionLocators);
        boolean found = false;

        for (WebElement option : options) {
            if (option.getText().trim().equalsIgnoreCase(optionToSelect)) {
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Option '" + optionToSelect + "' not found in dropdown.");
        }

        // Optional: verify selection
        String selected = findElement(selectedLocator).getAttribute("value");
        if (!selected.equalsIgnoreCase(optionToSelect)) {
            throw new IllegalStateException("Expected '" + optionToSelect + "', but found '" + selected + "'");
        }
    }

    public NewsItem extractNewsItemFromBlock(
            WebElement article,
            By titleLocator,
            By dateLocator,
            String datePattern,
            String origin,
            String type,
            boolean isTitleFromAriaLabel) {
        try {
            WebElement anchor = article.findElement(titleLocator);
            WebElement dateElement = article.findElement(dateLocator);

            String title = isTitleFromAriaLabel ? anchor.getAttribute("aria-label") : anchor.getText().trim();
            String url = anchor.getAttribute("href").trim();
            String rawDate = dateElement.getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
            LocalDate publishedDate = LocalDate.parse(rawDate, formatter);

            return new NewsItem(origin, publishedDate, type, title, url);
        } catch (Exception e) {
            System.err.println("Error extracting news item: " + e.getMessage());
            return null;
        }
    }

}

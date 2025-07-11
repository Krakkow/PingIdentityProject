package com.pingidentity.pageutils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.pingidentity.POJOs.WhoNewsItem;
import com.pingidentity.pages.WhoNewsPageElements;
import com.pingidentity.utils.WebDriverCommonFunc;

public class WhoNewsPageUtils {

    private final WebDriverCommonFunc utils;

    private static final String ORIGIN = "https://www.who.int";
    private static final DateTimeFormatter WHO_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");

    public WhoNewsPageUtils(WebDriverCommonFunc utils) {
        this.utils = utils;
    }

    public List<WhoNewsItem> getArticleByType(int maxCount, String expectedType) {
        filterByNewsType(expectedType);

        List<WebElement> articleBlocks = getVisibleArticles();
        List<WhoNewsItem> newsList = new ArrayList<>();

        int count = 0;
        for (WebElement article : articleBlocks) {
            if (count >= maxCount)
                break;

            WhoNewsItem item = extractWhoNewsItem(article, expectedType);
            if (item != null) {
                newsList.add(item);
                count++;
            }
        }
        return newsList;
    }

    private List<WebElement> getVisibleArticles() {
        return utils.findElements(WhoNewsPageElements.NEWS_ARTICLE_BLOCKS);
    }

    private WhoNewsItem extractWhoNewsItem(WebElement article, String type) {
        try {
            WebElement anchor = article.findElement(WhoNewsPageElements.ARTICLE_TITLE);
            WebElement dateElement = article.findElement(WhoNewsPageElements.ARTICLE_DATE);

            String title = anchor.getAttribute("aria-label");
            String url = anchor.getAttribute("href").trim();
            String rawDate = dateElement.getText().trim();
            LocalDate publishedDate = LocalDate.parse(rawDate, WHO_DATE_FORMATTER);

            return new WhoNewsItem(ORIGIN, publishedDate, type, title, url);

        } catch (Exception e) {
            System.err.println("Error extracting news item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Interacts with the dropdown to select a given news type (e.g., "Statement").
     */
    private void filterByNewsType(String typeToSelect) {
        // Open Dropdown
        utils.click(WhoNewsPageElements.NEWS_TYPE_DROPDOWN);
        // Get all Options from Drop down
        List<WebElement> options = utils.findElements(WhoNewsPageElements.NEWS_TYPE_DROPDOWN_OPTIONS);
        // Select the option that matches typeToSelect
        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().trim().equalsIgnoreCase(typeToSelect)) {
                option.click();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("News type '" + typeToSelect + "' not found in dropdown options.");
        }
        // Verify the selection is made correctly
        String selectedType = utils.findElement(WhoNewsPageElements.SELECTED_NEWS_TYPE).getAttribute("value");
        if (!selectedType.equalsIgnoreCase(typeToSelect)) {
            throw new IllegalStateException(
                    "Expected news type '" + typeToSelect + "' but found '" + selectedType + "'.");
        }
    }

}

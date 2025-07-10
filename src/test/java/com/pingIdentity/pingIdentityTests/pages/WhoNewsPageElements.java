package com.pingIdentity.pingIdentityTests.pages;

import org.openqa.selenium.By;

public class WhoNewsPageElements {

    public static final String EXPECTED_TAB_TITLE = "News";
    public static final String EXPECTED_URL = "https://www.who.int/news";

    public static final By PAGE_TITLE_HEADING = By.xpath("//h1[contains(text(), 'News')]");
    public static final By PAGE_SUBTITLE_HEADING = By.xpath("//p[contains(text(), 'Browse selected WHO news below' )]");
    public static final By NEWS_TYPE_DROPDOWN = By
            .xpath("//input[@placeholder='News type']/ancestor::span[contains(@class,'k-dropdown-wrap')]");
    public static final By NEWS_ARTICLE_BLOCKS = By
            .xpath("//div[contains(@class, 'list-view--item vertical-list-item')]");
    public static final By ARTICLE_TITLE = By.xpath(".//a[contains(@class, 'link-container')]");
    public static final By ARTICLE_URL = By.xpath(".//a[contains(@class, 'link-container')]");

}

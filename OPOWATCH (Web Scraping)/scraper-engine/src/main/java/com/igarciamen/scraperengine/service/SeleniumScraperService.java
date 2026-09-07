package com.igarciamen.scraperengine.service;

import com.igarciamen.scraperengine.payloads.request.SeleniumScrapeRequest;
import com.igarciamen.scraperengine.payloads.response.SeleniumPostingResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeleniumScraperService {

    private static final Logger log = LoggerFactory.getLogger(SeleniumScraperService.class);

    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(45);
    private static final Duration ELEMENT_WAIT_TIMEOUT = Duration.ofSeconds(30);

    public List<SeleniumPostingResponse> scrape(SeleniumScrapeRequest request) {
        WebDriver driver = newHeadlessDriver();
        List<SeleniumPostingResponse> result = new ArrayList<>();

        try {
            driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
            driver.get(request.getTargetUrl());

            By listBy = By.cssSelector(request.getListSelector());
            try {
                new WebDriverWait(driver, ELEMENT_WAIT_TIMEOUT)
                        .until(ExpectedConditions.presenceOfElementLocated(listBy));
            } catch (TimeoutException ex) {
                logDiagnostics(driver, request.getListSelector());
                throw ex;
            }

            List<WebElement> items = driver.findElements(listBy);

            for (WebElement item : items) {
                SeleniumPostingResponse posting = extractPosting(item, request);
                if (posting != null) {
                    result.add(posting);
                }
            }
        } finally {
            driver.quit();
        }

        return result;
    }

    private void logDiagnostics(WebDriver driver, String missingSelector) {
        try {
            String currentUrl = driver.getCurrentUrl();
            String title = driver.getTitle();
            String pageSource = driver.getPageSource();
            String snippet = pageSource.length() > 3000 ? pageSource.substring(0, 3000) : pageSource;

            log.error("Could not find selector '{}'. Current URL: {} | Page title: {}", missingSelector, currentUrl, title);
            log.error("First 3000 characters of the page HTML actually seen by Selenium:\n{}", snippet);
        } catch (Exception diagnosticFailure) {
            log.error("Could not even capture diagnostics: {}", diagnosticFailure.getMessage());
        }
    }

    private SeleniumPostingResponse extractPosting(WebElement item, SeleniumScrapeRequest request) {
        String title = findTextOrNull(item, request.getTitleSelector());
        String url = findAttributeOrNull(item, request.getLinkSelector(), "href");

        if (title == null || title.isBlank() || url == null || url.isBlank()) {
            return null;
        }

        String organization = request.getOrganizationSelector() != null
                ? findTextOrNull(item, request.getOrganizationSelector())
                : null;

        String publicationDate = request.getDateSelector() != null
                ? findTextOrNull(item, request.getDateSelector())
                : null;

        return new SeleniumPostingResponse(title.trim(), organization, publicationDate, url);
    }

    private String findTextOrNull(WebElement container, String selector) {
        if (selector == null || selector.isBlank()) {
            return null;
        }
        try {
            return container.findElement(By.cssSelector(selector)).getText();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    private String findAttributeOrNull(WebElement container, String selector, String attribute) {
        if (selector == null || selector.isBlank()) {
            return null;
        }
        try {
            return container.findElement(By.cssSelector(selector)).getAttribute(attribute);
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    protected WebDriver newHeadlessDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }
}
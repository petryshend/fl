package com.px;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mailer {

    private String username;
    private String password;

    private WebDriver driver;
    private String baseUrl;

    private List <String> womenIds;
    private List <String> womenSendIntroLinks;

    public Mailer() {
        this.driver = new FirefoxDriver();
        this.baseUrl = "http://www.foreignladies.com";

        this.username = "LugaAgency";
        this.password = "range683zip";

        this.womenIds = new ArrayList<String>();
        this.womenSendIntroLinks = new ArrayList<String>();
    }

    public void start() {

        JSONObject menCountryCriteria = Utils.getMenCountryCriteria();

        this.openHomePage();
        this.goToLoginPage();
        this.submitLoginForm();
        Utils.wait(2000);
        this.submitAgreeWithRulesForm();
        Utils.wait(2000);
        this.goToWomanList();
        this.collectWomenSearchProfileLinks();
        this.extractWomenIdsFromSearchProfileLinks();

        for (String womanLink: this.womenSendIntroLinks) {
            this.sendLettersForWoman(womanLink);
        }
    }

    private void openHomePage() {
        this.driver.get(this.baseUrl);
    }

    private void goToLoginPage() {
        String loginLinkText = "Affiliate Login";
        WebElement affiliateLoginLink = this.driver.findElement(By.partialLinkText(loginLinkText));
        affiliateLoginLink.click();
    }

    private void submitLoginForm() {
        WebElement usernameInput = this.driver.findElement(By.id("logins_ident"));
        usernameInput.sendKeys(this.username);

        WebElement passwordInput = this.driver.findElement(By.id("logins_password"));
        passwordInput.sendKeys(this.password);

        WebElement submitButton = this.driver.findElement(By.name("btn_submit"));
        submitButton.click();
    }
    

    private void submitAgreeWithRulesForm() {
        WebElement understandRadio = this.driver.findElement(By.cssSelector("input[value=\"yes\"]"));
        understandRadio.click();

        WebElement submitButton = this.driver.findElement(By.cssSelector("input[type=\"submit\"]"));
        submitButton.click();
    }

    private void goToWomanList() {
        String womenListUrl = "http://www.foreignladies.com/aff-assign_women~_step-500.html";
        this.driver.get(womenListUrl);
    }

    private void collectWomenSearchProfileLinks() {
        String linkSelector = "a[title=\"Send Intro\"]";
        List<WebElement> links = this.driver.findElements(By.cssSelector(linkSelector));

        for (WebElement element : links) {
            this.womenSendIntroLinks.add(element.getAttribute("href"));
        }
    }

    private void extractWomenIdsFromSearchProfileLinks() {
        if (this.womenSendIntroLinks.size() > 0) {
            Pattern p = Pattern.compile("[0-9]+");
            for (String link : this.womenSendIntroLinks) {
                Matcher m = p.matcher(link);
                if (m.find()) {
                    this.womenIds.add(m.group());
                }
            }
        }
    }

    private void sendLettersForWoman(String womanLink) {
        this.driver.get(womanLink);

        System.exit(0);
    }
}
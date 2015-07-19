package com.px;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
    private JSONObject menCountries;

    public Mailer()
    {
        this.driver = new FirefoxDriver();
        this.baseUrl = "http://www.foreignladies.com";

        this.username = "LugaAgency";
        this.password = "range683zip";

        this.womenIds = new ArrayList<String>();
        this.womenSendIntroLinks = new ArrayList<String>();
    }

    public void start()
    {

        this.menCountries = Utils.getMenCountryCriteria();

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

    private void openHomePage()
    {
        this.driver.get(this.baseUrl);
    }

    private void goToLoginPage()
    {
        String loginLinkText = "Affiliate Login";
        WebElement affiliateLoginLink = this.driver.findElement(By.partialLinkText(loginLinkText));
        affiliateLoginLink.click();
    }

    private void submitLoginForm()
    {
        WebElement usernameInput = this.driver.findElement(By.id("logins_ident"));
        usernameInput.sendKeys(this.username);

        WebElement passwordInput = this.driver.findElement(By.id("logins_password"));
        passwordInput.sendKeys(this.password);

        WebElement submitButton = this.driver.findElement(By.name("btn_submit"));
        submitButton.click();
    }
    

    private void submitAgreeWithRulesForm()
    {
        WebElement understandRadio = this.driver.findElement(By.cssSelector("input[value=\"yes\"]"));
        understandRadio.click();

        WebElement submitButton = this.driver.findElement(By.cssSelector("input[type=\"submit\"]"));
        submitButton.click();
    }

    private void goToWomanList()
    {
        String womenListUrl = "http://www.foreignladies.com/aff-assign_women~_step-500.html";
        this.driver.get(womenListUrl);
    }

    private void collectWomenSearchProfileLinks()
    {
        String linkSelector = "a[title=\"Send Intro\"]";
        List<WebElement> links = this.driver.findElements(By.cssSelector(linkSelector));

        for (WebElement element : links) {
            this.womenSendIntroLinks.add(element.getAttribute("href"));
        }
    }

    private void extractWomenIdsFromSearchProfileLinks()
    {
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

    private void sendLettersForWoman(String womanLink)
    {
        this.driver.get(womanLink);
        for (String country : this.menCountries.keySet()) {
            Utils.wait(500);
            JSONArray countryAgeRanges = this.getCountryRanges(country);
            for (int i = 0; i < countryAgeRanges.length(); i++) {
                System.out.println("Setting range for " + country + ": " + countryAgeRanges.get(i));
                WebElement countrySelect = driver.findElement(By.cssSelector("#fk_countries"));
                countrySelect.sendKeys(country);
                this.setAgeRange(countryAgeRanges.get(i));
                Utils.wait(500);
                this.checkOtherSearchCriteria();
                Utils.wait(500);
                this.clickSubmitSearchButton();
                this.sendLetters();
                Utils.wait(500);
                this.driver.get(womanLink); // go back for next criteria
                Utils.wait(500);
            }
        }
    }

    private void checkOtherSearchCriteria()
    {
        WebElement lastActivity = driver.findElement(By.cssSelector("select[name=last_activity]"));
        lastActivity.sendKeys("Less than 6 months ago");

        WebElement photos = driver.findElement(By.cssSelector("select[name=profile_default_pic]"));
        photos.sendKeys("Profiles with Photos");
    }

    private JSONArray getCountryRanges(String country)
    {
        JSONObject objCountry = this.menCountries.getJSONObject(country);
        return objCountry.getJSONArray("ranges");
    }

    private void setAgeRange(Object range)
    {
        JSONArray arr = new JSONArray(range.toString());

        WebElement from = this.driver.findElement(By.cssSelector("select[name=profile_age__GREATER_EQUAL]"));
        from.sendKeys(arr.get(0).toString());

        WebElement to = this.driver.findElement(By.cssSelector("select[name=profile_age__SMALLER_EQUAL]"));
        to.sendKeys(arr.get(1).toString());
    }

    private void sendLetters()
    {
        if (!this.hasMenSearchResults()) {
            return;
        }
        this.checkAllMen();
        Utils.wait(500);
        this.clickBigGreenButton();
        if (this.driver.getPageSource().contains("User does not want to receive intro letters!")) {
            return;
        }
        Utils.wait(500);
        this.selectIntroLetter();
        Utils.wait(1000);
        this.selectPhotoToAttach();
        this.clickSendMessageButton();
    }

    private void clickSubmitSearchButton()
    {
        WebElement submitButton = this.driver.findElement(By.cssSelector("input[name=btn_submit]"));
        submitButton.click();
    }

    private boolean hasMenSearchResults()
    {
        List<WebElement> noResultsErrorSpan = this.driver.findElements(By.cssSelector("span.error_star"));
        if (noResultsErrorSpan.size() != 0) {
            System.out.println("No results here");
            return false;
        }
        return true;
    }

    private void checkAllMen()
    {
        WebElement checkAll = this.driver.findElement(By.cssSelector("input.check_all"));
        checkAll.click();
    }

    private void clickBigGreenButton()
    {
        WebElement sendIntroButton = this.driver.findElement(By.partialLinkText("SEND INTRO TO SELECTED MEMBERS"));
        sendIntroButton.click();
    }

    private void selectIntroLetter()
    {
        WebElement selectIntroLetter = this.driver.findElement(By.cssSelector("select#intro_letter"));
        selectIntroLetter.sendKeys(Keys.ARROW_DOWN);
        selectIntroLetter.sendKeys(Keys.ENTER);
    }

    private void selectPhotoToAttach()
    {
        WebElement choosePhotosAttachedButton = this.driver.findElement(By.cssSelector("#choose_photos_attached"));
        choosePhotosAttachedButton.click();
        String checkboxSelector = ".photo_list_bottom input[type=checkbox]";
        List<WebElement> photoCheckboxes = this.driver.findElements(By.cssSelector(checkboxSelector));
        photoCheckboxes.get(0).click();
    }

    private void clickSendMessageButton()
    {
        WebElement sendMessageButton = this.driver.findElement(By.cssSelector("input[name=btn_submit]"));
        sendMessageButton.click();
    }
}
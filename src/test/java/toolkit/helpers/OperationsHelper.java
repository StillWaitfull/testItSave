package toolkit.helpers;

import composite.IPage;
import net.sourceforge.htmlunit.corejs.javascript.JavaScriptException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.Assertion;
import toolkit.driver.LocalDriverManager;
import toolkit.driver.WebDriverController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OperationsHelper {

    private static Logger log = Logger.getLogger(OperationsHelper.class);
    private WebDriverWait waitDriver = WebDriverController.getInstanceWaitDriver();
    private WebDriverController driver = LocalDriverManager.getDriverController();
    public static String baseUrl;


    public static void initBaseUrl() {
        String host = System.getenv("host");
        if (host == null)
            baseUrl = YamlConfigProvider.getStageParameters("baseUrl");

    }


    /**
     * Exits from current user
     */
    public static void logoutHook() {
        if (LocalDriverManager.getDriverController() != null) {
            LocalDriverManager.getDriverController().goToUrl(baseUrl);
            LocalDriverManager.getDriverController().deleteAllCookies();
        }

    }

    public OperationsHelper openPage(IPage page) {
        openUrl(page.getPageUrl());
        return this;
    }


    public OperationsHelper pressEnter() {
        Actions action = new Actions(LocalDriverManager.getDriverController().getDriver());
        action.sendKeys(Keys.ENTER).perform();
        return this;

    }

    private boolean isEnable(By by) {
        try {
            if (driver.findElements(by).size() > 0)
                if (driver.findElement(by).isEnabled())
                    return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public WebElement findElement(By by) {
        validateElementVisible(by);
        return driver.findElement(by);
    }


    public static String getRandomEmail() {
        long currentTime = System.nanoTime();
        String longNumber = Long.toString(currentTime);
        return "notifytest." + "1" + longNumber + "@test.ru";
    }

    public static String getRandomLogin() {
        long currentTime = System.nanoTime();
        String longNumber = String.valueOf(currentTime);
        return "login" + longNumber.substring(4, 9);
    }

    public OperationsHelper windowSetSize(Dimension windowSize) {
        WebDriver.Window window = driver.getDriver().manage().window();
        Dimension size = window.getSize();
        log.debug("Current windowSize = " + size);
        window.setSize(windowSize);
        log.debug("New windowSize = " + size);
        return this;
    }

    protected String getAlertText() {
        // Get a handle to the open alert, prompt or confirmation
        Alert alert = driver.getDriver().switchTo().alert();
        // Get the text of the alert or prompt
        return alert.getText();
    }

    protected OperationsHelper clickOkInAlert() {
        // Get a handle to the open alert, prompt or confirmation
        Alert alert = driver.getDriver().switchTo().alert();
        // Get the text of the alert or prompt
        log.debug("alert: " + alert.getText());
        // And acknowledge the alert (equivalent to clicking "OK")
        alert.accept();
        return this;
    }


    public OperationsHelper windowSetSize(int widthWindow, int heightWindow) {
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (((screenSize.width >= widthWindow) && (screenSize.height >= heightWindow)) ||
                ((0 >= widthWindow) && (0 >= heightWindow))) {
            Dimension targetSize = new Dimension(widthWindow, heightWindow);
            windowSetSize(targetSize);
        } else {
            log.debug("it is impossible");
        }
        return this;
    }

    protected boolean waitForElementPresent(By by) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (isElementPresent(by)) {
                return true;
            }
            sendPause(1);
        }

        return false;
    }


    public OperationsHelper waitElementForSec(By by, int seconds) {
        for (int i = 0; i < seconds; i++) {
            if (findElement(by).isDisplayed()) break;
             else sendPause(1);
        }
        return this;
    }


    public OperationsHelper waitStalenessElement(final By by) {
        waitDriver.until((WebDriver webDriver) -> {
            try {
                final WebElement element = driver.findElement(by);
                if (element != null && element.isDisplayed()) {
                    return element;
                }
            } catch (StaleElementReferenceException e) {
                log.error("Stale exception");
            }
            return null;
        });
        return this;
    }


    public OperationsHelper clickOnStalenessElement(final By by) {
        waitDriver.until((WebDriver webDriver) -> {
            try {
                final WebElement element = driver.findElement(by);
                if (element != null && element.isDisplayed() && element.isEnabled()) {
                    element.click();
                    return element;
                }
            } catch (StaleElementReferenceException e) {
                log.error("Stale exception");
            }
            return null;
        });
        return this;
    }

    protected OperationsHelper clickCancelInAlert() {
        // Get a handle to the open alert, prompt or confirmation
        Alert alert = driver.getDriver().switchTo().alert();
        // Get the text of the alert or prompt
        log.debug("alert: " + alert.getText());
        // And acknowledge the alert (equivalent to clicking "Cancel")
        alert.dismiss();
        return this;
    }

    protected OperationsHelper waitForNotAttribute(final By by, final String attribute, final String value) {
        waitDriver.until((WebDriver d) -> d.findElement(by)
                .getAttribute(attribute)
                .equals(value));
        return this;
    }


    protected OperationsHelper waitForTextPresent(final String text) {
        waitDriver.until((WebDriver d) -> d.getPageSource().contains(text));
        return this;
    }


    public java.util.List<WebElement> findElements(final By by) {
        validateElementPresent(by);
        return driver.findElements(by);
    }


    protected boolean waitForVisible(By by) {
        return waitForVisible(by, true);
    }

    protected boolean waitForNotVisible(By by) {
        return waitForVisible(by, false);
    }


    private boolean waitForVisible(By by, boolean isVisible) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (isVisible(by) && isVisible) {
                return true;
            } else if (!isVisible(by) && !isVisible) {
                return true;
            }
            sendPause(1);
        }
        return false;
    }

    public static void sendPause(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException iex) {
            Thread.interrupted();
        }

    }

    public String getSrcOfElement(By by) {
        return driver.findElement(by).getAttribute("src");
    }


    public double getNumberFromElement(By by) {
        Pattern PATTERN = Pattern.compile("\\d+");
        Matcher MATCHER = PATTERN.matcher(getText(by));
        boolean result = MATCHER.find();
        return result ? Double.parseDouble(MATCHER.group(0)) : 0;
    }


    /**
     * Gets the absolute URL of the current page.
     *
     * @return the absolute URL of the current page
     */
    public String getCurrentUrl() {
        return driver.getDriver().getCurrentUrl();
    }

    protected OperationsHelper selectValueInDropDown(By by, String optionValue) {
        Select select = new Select(driver.findElement(by));
        select.selectByValue(optionValue);
        return this;
    }


    public OperationsHelper submit(By by) {
        log.debug("Submit:");
        waitForElementPresent(by);
        driver.findElement(by).submit();
        return this;

    }


    /**
     * Sends to API browser command back
     */
    public OperationsHelper navigateBack() {
        driver.navigationBack();
        return this;
    }


    public OperationsHelper moveToElement(By by) {
        Actions actions = new Actions(driver.getDriver());
        waitForElementPresent(by);
        actions.moveToElement(driver.findElement(by)).build().perform();
        return this;
    }

    public OperationsHelper mouseClick() {
        Actions actions = new Actions(driver.getDriver());
        actions.click().build().perform();
        return this;
    }


    public void highlightTheElement(By by) {
        WebElement element = driver.findElement(by);
        driver.executeScript("arguments[0].style.border='2px solid yellow'", element);
    }


    public OperationsHelper click(By by) {
        log.debug("Click on: " + by.toString());
        validateElementVisible(by);
        highlightTheElement(by);
        driver.findElement(by).click();
        return this;
    }

    public Assertion assertThat(){
     return new Assertion();
    }



    public String getText(By by) {
        log.debug("Text from: " + by.toString());
        waitForVisible(by);
        return driver.findElement(by).getText();
    }


    public String getAttribute(By by, String nameAttribute) {
        log.debug("Text from: " + by.toString());
        waitForVisible(by);
        return driver.findElement(by).getAttribute(nameAttribute);
    }


    public String getPageSource() {
        return driver.getDriver().getPageSource();
    }


    protected OperationsHelper selectWindow(String windowId) {
        for (String handle : driver.getWindowHandles()) {
            if (handle.equals(windowId)) {
                driver.switchToWindow(handle);
                break;
            }
        }
        return this;
    }

    protected OperationsHelper selectOtherWindow() {
        String current = driver.getWindowHandle();
        int timer = 0;
        while (timer < WebDriverController.TIMEOUT) {
            if (driver.getWindowHandles().size() > 1)
                break;
            else {
                sendPause(1);
                timer++;
            }

        }
        for (String handle : driver.getWindowHandles()) {
            try {
                if (!handle.equals(current))
                    driver.switchToWindow(handle);
            } catch (Exception e) {
                Assert.fail("Unable to select window");
            }
        }

        return this;
    }

    protected OperationsHelper selectWindowAndCloseCurrent() {
        String current = driver.getWindowHandle();
        driver.closeWindow();
        int timer = 0;
        while (timer < WebDriverController.TIMEOUT) {
            if (driver.getWindowHandles().size() > 1)
                break;
            else {
                sendPause(1);
                timer++;
            }

        }
        for (String handle : driver.getWindowHandles()) {
            try {
                if (!handle.equals(current))
                    driver.switchToWindow(handle);
            } catch (Exception e) {
                Assert.fail("Unable to select window");
            }
        }
        return this;
    }


    public boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException se) {
            return false;
        }
    }


    public boolean isVisible(By by) {
        try {
            if (driver.findElements(by).size() > 0)
                return driver.findElement(by).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException se) {
            return false;
        }
        return false;
    }


    public OperationsHelper type(By by, String someText) {
        log.debug("Type:" + someText + " to:" + by.toString());
        validateElementVisible(by);
        highlightTheElement(by);
        driver.findElement(by).clear();
        driver.findElement(by).sendKeys(someText);
        return this;
    }


    public static void makeScreenshot(String methodName) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture;
        try {
            capture = new Robot().createScreenCapture(screenRect);
            File fileScreenshot = new File("target" + File.separator + "failure_screenshots" +
                    File.separator + methodName + "_" + formater.format(calendar.getTime()) + "_javarobot.jpg");
            fileScreenshot.getParentFile().mkdirs();
            ImageIO.write(capture, "jpg", fileScreenshot);
            File scrFile = ((TakesScreenshot) LocalDriverManager.getDriverController().getDriver()).getScreenshotAs(
                    OutputType.FILE);
            FileUtils.copyFile(scrFile, new File("target" + File.separator + "failure_screenshots" +
                    File.separator + methodName + "_" + formater.format(calendar.getTime()) + "_webdriver.png"));
        } catch (AWTException | IOException awte) {
            awte.printStackTrace();
        }

    }

    /**
     * Open page
     */
    public OperationsHelper openUrl(String url) {
        log.info("Open page: " + url);
        driver.get(url);
        return this;
    }

    public OperationsHelper openUriWithCurrentUrl(String uri) {
        openUrl(driver.getPageAddress() + uri);
        return this;
    }


    /**
     * Open url in new window
     */
    public OperationsHelper openInNewWindow(String url) {
        log.info("Open page in new window : " + url);
        openTab(url);
        selectOtherWindow();
        return this;
    }


    /**
     * Opens a new tab for the given URL
     *
     * @param url The URL to
     * @throws JavaScriptException If unable to open tab
     */
    public OperationsHelper openTab(String url) {
        String script = "var d=document,a=d.createElement('a');a.target='_blank';a.href='%s';a.innerHTML='.';d.body.appendChild(a);return a";
        Object element = LocalDriverManager.getDriverController().executeScript(String.format(script, url));
        if (element instanceof WebElement) {
            WebElement anchor = (WebElement) element;
            anchor.click();
            LocalDriverManager.getDriverController().executeScript("var a=arguments[0];a.parentNode.removeChild(a);",
                    anchor);
        } else {
            throw new JavaScriptException(element, "Unable to open tab", 1);
        }
        return this;
    }

    public Object getLastElement(final Collection c) {
        final Iterator itr = c.iterator();
        Object lastElement = itr.next();
        while (itr.hasNext()) {
            lastElement = itr.next();
        }
        return lastElement;
    }


    public boolean validateElementAttribute(By by, String attribute, String value) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (findElement(by).getAttribute(attribute).equals(value)) {
                return true;
            }
            sendPause(1);
        }
        return false;
    }


    public boolean validateElementPresent(By by) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (isElementPresent(by)) {
                return true;
            }
            sendPause(1);
        }

        return false;
    }


    public boolean validateElementIsNotVisible(By by) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (!isVisible(by))
                return true;
            sendPause(1);
        }
        return false;
    }


    public boolean validateElementVisible(By by) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (isVisible(by))
                return true;
            sendPause(1);
        }
        return false;

    }

    public boolean validateUrlContains(String s) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (getCurrentUrl().contains(s))
                return true;
            sendPause(1);
        }
        return false;

    }


    public boolean validateElementEnable(By by) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (isEnable(by))
                return true;
            sendPause(1);
        }
        return false;

    }

    public boolean validateTextEquals(By by, String text) {
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (getText(by).equals(text))
                return true;
            sendPause(1);
        }
        return false;

    }

    public boolean validateTextNotEquals(By by, String text) {
        validateElementVisible(by);
        for (int i = 0; i < WebDriverController.TIMEOUT; i++) {
            if (!getText(by).equals(text))
                return true;
            sendPause(1);
        }
        return false;

    }

    /**
     * Reloads page
     */
    public OperationsHelper refreshPage() {
        driver.refresh();
        return this;
    }


    // Set a cookie
    public OperationsHelper addCookie(String key, String value) {
        driver.addCookie(key, value);
        return this;
    }


    public OperationsHelper switchTo(String iFrame) {
        sendPause(2); // for correct open iFrame and switch there
        driver.switchTo(iFrame);
        return this;
    }


    public OperationsHelper hoverOn(By by) {
        Actions action = new Actions(driver.getDriver());
        action.moveToElement(findElement(by)).build().perform();
        log.info("Action - hover on to locator: " + by.toString());
        return this;
    }


    public OperationsHelper scrollOnTop() {
        LocalDriverManager.getDriverController().executeScript("window.scrollTo(0,0)");
        return this;
    }


    /**
     * Returns count of elements on a page with this locator
     */
    public int getCountElements(By by) {
        waitForElementPresent(by);
        return driver.findElements(by).size();
    }


    public static Date getChangedDate(int countDay) {
        Calendar calendar = Calendar.getInstance();
        Date lifeTimeDateFinish = new Date();
        calendar.setTime(lifeTimeDateFinish);
        calendar.add(Calendar.DATE, countDay);
        return calendar.getTime();
    }


}

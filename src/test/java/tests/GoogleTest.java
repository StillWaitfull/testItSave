package tests;

import composite.pages.GooglePage;
import org.testng.annotations.Test;


public class GoogleTest extends AbstractTest {

    @Test
    public void googleTest() {
        GooglePage googlePage = new GooglePage();
        googlePage.openPage(googlePage)
                .type(GooglePage.query, "0")
                .click(GooglePage.button)
                .assertThat()
                .assertTrue(googlePage.isVisible(GooglePage.button), "Google button is not visible");
    }


    @Test
    public void googleTest1() {
        GooglePage googlePage = new GooglePage();
        googlePage.openPage(googlePage)
                .type(GooglePage.query, "1")
                .click(GooglePage.button);


    }

    @Test
    public void googleTest2() {
        GooglePage googlePage = new GooglePage();
        googlePage.openPage(googlePage)
                .type(GooglePage.query, "2")
                .click(GooglePage.button);
    }

    @Test
    public void googleTest3() {
        GooglePage googlePage = new GooglePage();
        googlePage.openPage(googlePage)
                .type(GooglePage.query, "3")
                .click(GooglePage.button);
    }


}

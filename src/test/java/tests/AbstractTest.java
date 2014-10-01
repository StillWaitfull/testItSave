package tests;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;
import toolkit.Security.ZapProxy;
import toolkit.driver.LocalDriverManager;
import toolkit.driver.WebDriverListener;
import toolkit.helpers.OperationsHelper;


/**
 * Abstract test
 *
 * @author Aleksey Niss,Sergey Kashapov
 */
@Listeners(WebDriverListener.class)
public abstract class AbstractTest {

    protected static Logger log4j = Logger.getLogger(AbstractTest.class);


    public AbstractTest() {
        ZapProxy.run();
        OperationsHelper.initBaseUrl();
    }



    @AfterMethod
    public void after() {
        if (LocalDriverManager.getDriverController() != null)
            LocalDriverManager.getDriverController().deleteAllCookies();
    }

    @AfterSuite
    public void cleanPool() {
        LocalDriverManager.cleanThreadPool();
        ZapProxy.execute(OperationsHelper.baseUrl);
    }
}

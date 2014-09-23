package toolkit.driver;

import java.util.concurrent.ConcurrentHashMap;

public class LocalDriverManager {

    private static ConcurrentHashMap<Thread, WebDriverController> webDrivers = new ConcurrentHashMap<>();

    public static WebDriverController getDriverController() {

        return webDrivers.get(Thread.currentThread());
    }

    public static void setWebDriverController(WebDriverController driver) {
        webDrivers.put(Thread.currentThread(), driver);
    }

    public static void cleanThreadPool() {
        for (WebDriverController controller : webDrivers.values())
            controller.shutdown();
        for (Thread cur : webDrivers.keySet())
            cur.interrupt();
        webDrivers.clear();
    }
}

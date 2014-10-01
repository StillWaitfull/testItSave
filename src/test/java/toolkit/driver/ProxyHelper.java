package toolkit.driver;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import toolkit.helpers.YamlConfigProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ProxyHelper {


    static final Integer proxyPort = Integer.valueOf(YamlConfigProvider.getAppParameters("proxyPort"));
    static ProxyServer server = new ProxyServer(proxyPort);
    static boolean needProxy = Boolean.parseBoolean(YamlConfigProvider.getAppParameters("enableProxy"));
    static boolean zapScan = Boolean.parseBoolean(YamlConfigProvider.getAppParameters("zapScan"));
    private static Proxy proxy = new Proxy();
    static String host=YamlConfigProvider.getAppParameters("host");


    public static void initProxy() {
        if (needProxy) {
            try {
                server.start();
                server.setRequestTimeout(WebDriverController.TIMEOUT * 1000);
                proxy = server.seleniumProxy();
            }
            catch (Exception e) {e.printStackTrace();}
        }
        else if (zapScan) {
            proxy.setHttpProxy(host + ":" + proxyPort);
            proxy.setSslProxy(host + ":" + proxyPort);
        }
        else {
            proxy.setAutodetect(true);
        }
    }



    public static void setCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        if (needProxy)  server.newHar(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
    }


    public static void saveHarToDisk(String name) {
        Har har = server.getHar();
        try {
            File file = new File("target" + File.separator + "hars" + File.separator + name + ".har");
            file.getParentFile().mkdirs();
            har.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

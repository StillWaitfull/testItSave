package toolkit.driver;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
import org.apache.log4j.Logger;
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

    static Logger log = Logger.getLogger(ProxyHelper.class);
    static final Integer proxyPort = Integer.valueOf(YamlConfigProvider.getAppParameters("proxyPort"));
    static ProxyServer server = new ProxyServer(proxyPort);
    static boolean needProxy = Boolean.parseBoolean(YamlConfigProvider.getAppParameters("enableProxy"));
    private static Proxy proxy = null;


    public static void initProxy() {
        if (System.getenv("enableProxy") != null)
            needProxy = Boolean.parseBoolean(System.getenv("enableProxy"));
        if (needProxy) {
            try {
                //  server.setLocalHost(InetAddress.getByName("127.0.0.1"));
                server.start();
                server.setRequestTimeout(WebDriverController.TIMEOUT * 1000);
                proxy = server.seleniumProxy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        initProxy();
    }


    public static void stopProxy() {
        if (needProxy) {
            try {
                server.stop();
            } catch (Exception e) {
                log.error("There was a problem with shutdown proxy server");
                e.printStackTrace();
            }
        }
    }


    public static void setCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        server.newHar(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
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

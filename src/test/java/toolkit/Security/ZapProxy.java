package toolkit.Security;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.zaproxy.clientapi.core.*;
import toolkit.helpers.OperationsHelper;
import toolkit.helpers.YamlConfigProvider;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Restore on 10/1/14.
 */
public class ZapProxy {

    private static String zapProxyHost = YamlConfigProvider.getAppParameters("host");
    private static int zapProxyPort = Integer.parseInt(YamlConfigProvider.getAppParameters("proxyPort"));
    private static String zapProgram = YamlConfigProvider.getAppParameters("zapProgram");
    private static String reportsDirectory = YamlConfigProvider.getAppParameters("reportsDirectory");
    private static String format = YamlConfigProvider.getAppParameters("reportFormat");
    private final static String MEDIUM = "MEDIUM";
    private final static String HIGH = "HIGH";
    static Logger log = Logger.getLogger(ZapProxy.class);
    private static ClientApi zapClientAPI;




    public static void run() {
        if(!Boolean.parseBoolean(YamlConfigProvider.getAppParameters("zapScan")))
            return;
        try {
            File pf = new File(zapProgram);
            log.info("Start ZAProxy [" + zapProgram + "]");
            log.info("Using working directory [" + pf.getParentFile().getPath() + "]");
            final Process ps = Runtime.getRuntime().exec(zapProgram, null, pf.getParentFile());
            ps.waitFor(10, TimeUnit.SECONDS);
            zapClientAPI = new ClientApi(zapProxyHost, zapProxyPort);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Unable to start ZAP [" + zapProgram + "]");
        }

    }


    public static void execute(String url) {
        boolean check = true;
        if(!Boolean.parseBoolean(YamlConfigProvider.getAppParameters("zapScan")))
            return;
        setAlertAndAttackStrength();
        spiderWithZap(url);
        scanWithZap(url);
        reportAlerts();
        try { check =checkAlerts(zapClientAPI.getAlerts("", -1, -1));}
        catch (ClientApiException e) {e.printStackTrace();}
        shutdown();
        Assert.assertTrue(check,"There is HIGH Risk alerts in your target");

    }

    private static void spiderWithZap(String url) {
        try {
            log.info("Spidering...");
            zapClientAPI.spider.setOptionThreadCount(5);
            zapClientAPI.spider.setOptionMaxDepth(1);
            zapClientAPI.spider.setOptionPostForm(false);
            zapClientAPI.spider.scan(url);
            while (statusToInt(zapClientAPI.spider.status()) < 100)
                OperationsHelper.sendPause(3);
            log.info("Spider done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scanWithZap(String url) {
        log.info("Scanning...");
        try {
            zapClientAPI.ascan.scan(url, "true", "true");
            int complete = 0;
            while (complete < 100) {
                complete = statusToInt(zapClientAPI.ascan.status());
                log.info("Scan is " + complete + "% complete.");
                OperationsHelper.sendPause(3);
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
        log.info("Scanning done.");
    }


    private static void setAlertAndAttackStrength() {
        try {
            zapClientAPI.ascan.setOptionAttackStrength(MEDIUM);
            zapClientAPI.ascan.setOptionAlertThreshold(MEDIUM);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }



    private static void shutdown() {
        try {
            zapClientAPI.core.shutdown();
            log.info("Shutdown ZAProxy");
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }




    private static boolean checkAlerts(List<Alert> alerts) {
        List<Alert> filtered = new ArrayList<>();
            filtered.addAll(alerts.stream().filter(alert -> alert.getRisk().equals(Alert.Risk.High) && alert.getReliability() != Alert.Reliability.Suspicious).collect(Collectors.toList()));
        return filtered.size()==0;
    }


    private static String getAllAlerts(String format) throws Exception {
        URL url = null;
        String result = "";
        if (format.equalsIgnoreCase("xml") || format.equalsIgnoreCase("html") || format.equalsIgnoreCase("json"))
            url = new URL("http://zap/" + format + "/core/view/alerts");
        assert url != null;
        HttpURLConnection uc = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(zapProxyHost, zapProxyPort)));
        uc.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            result = result + inputLine;
        in.close();
        return result;

    }

    private static void reportAlerts() {
        String fileNameNoExtension = FilenameUtils.concat("target" + File.separator + reportsDirectory, "report");
        String forXml = "?>";
        try {
            String alerts = getAllAlerts(format);
            String fullFileName = fileNameNoExtension + "." + format;
            int index = alerts.indexOf(forXml) + forXml.length();
            FileUtils.writeStringToFile(new File(fullFileName), alerts.substring(0, index) + "<?xml-stylesheet href=\"../../src/test/resources/styles/ZAP.xsl\" type=\"text/xsl\" ?>" + alerts.substring(index));
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Source xslDoc = new StreamSource("src/test/resources/xsl/ZAP.xsl");
            Source xmlDoc = new StreamSource(fullFileName);
            OutputStream htmlFile = new FileOutputStream("target" + File.separator + reportsDirectory + File.separator + "Zap.html");
            Transformer transform = tFactory.newTransformer(xslDoc);
            transform.transform(xmlDoc, new StreamResult(htmlFile));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }



    private static int statusToInt(ApiResponse response) {
        return Integer.parseInt(((ApiResponseElement) response).getValue());
    }

}

package toolkit.helpers;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import toolkit.METHODS;

import java.util.List;


public class RequestClient {
    private String responseText = "";
    private String baseUrl = YamlConfigProvider.getStageParameters("baseUrl");
    static Logger log = Logger.getLogger(RequestClient.class);
    String requestLine = "";


    public RequestClient sendRequest(METHODS method, String url, List<BasicNameValuePair> params) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(baseUrl).setPath(url);
        for (BasicNameValuePair param : params)
            builder.setParameter(param.getName(), param.getValue());
        try {
            HttpRequestBase request = method.getMethod(builder.build());
            requestLine = request.getRequestLine().toString();
            log.info("Request is " + requestLine);
            HttpResponse response = HttpClientBuilder.create().build().execute(request);
            responseText = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.info("Response is " + responseText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    public RequestClient sendPostRequestWithEntity(String url, List<BasicNameValuePair> params, String token) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(baseUrl).setPath(url);
        builder.setParameter("token", token);
        try {
            HttpPost request = new HttpPost(builder.build());
            request.setEntity(new UrlEncodedFormEntity(params));
            log.info("Request is " + request.getRequestLine());
            HttpResponse response = HttpClientBuilder.create().build().execute(request);
            responseText = new BasicResponseHandler().handleResponse(response);
            log.info("Response is " + responseText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


}

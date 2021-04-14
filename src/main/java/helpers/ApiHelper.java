package helpers;

import com.jayway.jsonpath.JsonPath;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ApiHelper {

    private String clientSecret;
    private String clientID;
    private CloseableHttpClient httpClient;
    private HttpClientContext httpContext;
    private BasicCookieStore cookieStore;
    private Header bearerHeader;
    private String oauthURL = "https://allegro.pl/auth/oauth/token?grant_type=client_credentials";
    private String getCategoriesURL = "https://api.allegro.pl/sale/categories/";

    public ApiHelper(){
        clientSecret = "lBH2KXTjTXCGzRO7yfVKoH5SUsXoQS1Vjx7skTKCGAbBt7lTwlW54XFUH9xCm2n8";
        clientID = "a6f3e16e6a6f441cb5aa2a1e95acd172";
        var targetHost = new HttpHost("allegro",443,"https");
        httpClient = HttpClientBuilder.create().evictExpiredConnections().build();
        httpContext = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        httpContext.setCookieStore(cookieStore);
        var authCache = new BasicAuthCache();
        authCache.put(targetHost,new BasicScheme());
        httpContext.setAuthCache(authCache);
    }

    public void setToken() throws URISyntaxException, IOException {
        var post = new HttpPost(new URI(oauthURL));
        var authorizationValue="Basic " + Base64.getEncoder().encodeToString((clientID+":"+clientSecret).getBytes());
        post.addHeader("Authorization",authorizationValue);
        var response = httpClient.execute(post,httpContext);
        var jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        var accessToken = jsonObject.get("access_token").toString();
        bearerHeader = new BasicHeader("Authorization", "Bearer "+accessToken);
    }


    public List<Map<String, Object>> getListing() throws URISyntaxException, IOException {
        List<Map<String,Object>> testMap = new ArrayList<>();
        var get = new HttpGet(new URI(getCategoriesURL));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get,httpContext);
        var content = response.getEntity().getContent();
        var stringResponse = new BufferedReader(new InputStreamReader(content, UTF_8)).lines().collect(Collectors.joining());
        response.close();
        List<Map<String,Object>> map =JsonPath.read(stringResponse,"$.categories");
        map.forEach(s->testMap.add(Map.of("id",s.get("id"),"name",s.get("name"))));
        return testMap;
    }

    public Map<String, Object> getCategoryByID(String id) throws URISyntaxException, IOException {
        var get = new HttpGet(new URI(getCategoriesURL+id));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get,httpContext);
        var content = response.getEntity().getContent();
        var stringResponse = new BufferedReader(new InputStreamReader(content, UTF_8)).lines().collect(Collectors.joining());
        response.close();
        Map<String,Object> map =JsonPath.read(stringResponse,"$");
        return map;
    }

    public int getPostCategoryParametersStatus(String id) throws IOException, URISyntaxException {
        var response = getStatusAfterSendingPostRequest(getCategoriesURL+id+"/parameters");
        response.close();
        return response.getStatusLine().getStatusCode();
    }

    public int getGetCategoryParametersStatus(String id) throws IOException, URISyntaxException {
        var response = getStatusAfterSendingGetRequest(getCategoriesURL+id+"/parameters");
        return response.getStatusLine().getStatusCode();
    }

    public int getDeleteCategoryParametersStatus(String id) throws IOException, URISyntaxException {
        var response= getStatusAfterSendingDeleteRequest(getCategoriesURL+id+"/parameters");
        response.close();
        return response.getStatusLine().getStatusCode();
    }

    public Map<String, Object> getCategoryParameters(String id) throws URISyntaxException, IOException {
        var response = getStatusAfterSendingPostRequest(getCategoriesURL+id+"/parameters");
        var content = response.getEntity().getContent();
        var stringResponse = new BufferedReader(new InputStreamReader(content, UTF_8)).lines().collect(Collectors.joining());
        response.close();
        Map<String,Object> map =JsonPath.read(stringResponse,"$");
        return map;
    }

    private CloseableHttpResponse getStatusAfterSendingPostRequest(String url) throws URISyntaxException, IOException {
        var post = new HttpPost(new URI(url));
        post.addHeader(bearerHeader);
        return httpClient.execute(post,httpContext);
    }

    private CloseableHttpResponse getStatusAfterSendingGetRequest(String url) throws URISyntaxException, IOException {
        var get = new HttpGet(new URI(url));
        get.addHeader(bearerHeader);
        return httpClient.execute(get,httpContext);
    }

    private CloseableHttpResponse getStatusAfterSendingDeleteRequest(String url) throws URISyntaxException, IOException {
        var delete = new HttpDelete(new URI(url));
        delete.addHeader(bearerHeader);
        return httpClient.execute(delete,httpContext);
    }
}

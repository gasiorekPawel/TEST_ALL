package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.jayway.jsonpath.JsonPath;
import entitles.category.CategoryRootResponse;
import entitles.categorybyid.CategoryResponse;
import entitles.categoryparameters.CategoryParametersResponse;
import org.apache.http.Header;
import org.apache.http.HttpHost;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static helpers.TestConstants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ApiHelper {

    private final CloseableHttpClient httpClient;
    private final HttpClientContext httpContext;
    private final BasicCookieStore cookieStore;
    private final String oauthURL = "https://allegro.pl/auth/oauth/token?grant_type=client_credentials";
    private final String getCategoriesURL = "https://api.allegro.pl/sale/categories/";
    private final String clientSecret = "lBH2KXTjTXCGzRO7yfVKoH5SUsXoQS1Vjx7skTKCGAbBt7lTwlW54XFUH9xCm2n8";
    private final String clientID = "a6f3e16e6a6f441cb5aa2a1e95acd172";
    private Header bearerHeader;

    public ApiHelper() {
        var targetHost = new HttpHost("allegro", 443, "https");
        httpClient = HttpClientBuilder.create().evictExpiredConnections().build();
        httpContext = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        httpContext.setCookieStore(cookieStore);
        var authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        httpContext.setAuthCache(authCache);
    }

    private String parseInputStreamToString(InputStream input) {
        return new BufferedReader(new InputStreamReader(input, UTF_8)).lines().collect(Collectors.joining());
    }

    public String getGetCategoriesURL() {
        return getCategoriesURL;
    }

    public String getCategoryByIDURL(String id) {
        return getCategoriesURL + id;
    }

    public String getCategoryParametersByIDURL(String id) {
        return getCategoryByIDURL(id) + "/parameters";
    }

    public void setToken() throws URISyntaxException, IOException {
        var post = new HttpPost(new URI(oauthURL));
        var authorizationValue = "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes());
        post.addHeader(AUTHORIZATION, authorizationValue);
        var response = httpClient.execute(post, httpContext);
        var jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        var accessToken = jsonObject.get("access_token").toString();
        bearerHeader = new BasicHeader(AUTHORIZATION, "Bearer " + accessToken);
    }


    public List<Map<String, Object>> getListing() throws URISyntaxException, IOException {
        List<Map<String, Object>> testMap = new ArrayList<>();
        var get = new HttpGet(new URI(getCategoriesURL));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get, httpContext);
        var content = response.getEntity().getContent();
        var stringResponse = parseInputStreamToString(content);
        response.close();
        List<Map<String, Object>> map = JsonPath.read(stringResponse, "$.categories");
        map.forEach(s -> testMap.add(Map.of(ID, s.get(ID), NAME, s.get(NAME))));
        return testMap;
    }

    public Map<String, Object> getCategoryByID(String id) throws URISyntaxException, IOException {
        var get = new HttpGet(new URI(getCategoryByIDURL(id)));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get, httpContext);
        var content = response.getEntity().getContent();
        var stringResponse = parseInputStreamToString(content);
        response.close();
        Map<String, Object> map = JsonPath.read(stringResponse, "$");
        return map;
    }

    public boolean verifyGetCategoryByIdResponseContent(String id) throws IOException, URISyntaxException {
        var get = new HttpGet(new URI(getCategoryByIDURL(id)));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get, httpContext);
        var content = response.getEntity().getContent();
        try {
            new ObjectMapper().readValue(content, CategoryResponse.class);
            return true;
        } catch (UnrecognizedPropertyException ex) {
            return false;
        }
    }

    public boolean verifyGetCategoryParametersResponseContent(String id) throws IOException, URISyntaxException {
        var get = new HttpGet(new URI(getCategoryParametersByIDURL(id)));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get, httpContext);
        var content = response.getEntity().getContent();
        try {
            new ObjectMapper().readValue(content, CategoryParametersResponse.class);
            return true;
        } catch (UnrecognizedPropertyException ex) {
            return false;
        }
    }

    public boolean verifyGetMainCategoriesResponseContent() throws IOException, URISyntaxException {
        var get = new HttpGet(new URI(getCategoriesURL));
        get.addHeader(bearerHeader);
        var response = httpClient.execute(get, httpContext);
        var content = response.getEntity().getContent();
        try {
            new ObjectMapper().readValue(content, CategoryRootResponse.class);
            return true;
        } catch (UnrecognizedPropertyException ex) {
            return false;
        }
    }

    public CloseableHttpResponse getStatusFromGetRequest(String url) throws IOException, URISyntaxException {
        var response = getStatusAfterSendingGetRequest(url);
        response.close();
        return response;
    }

    public CloseableHttpResponse getStatusFromPostRequest(String url) throws IOException, URISyntaxException {
        var response = getStatusAfterSendingPostRequest(url);
        response.close();
        return response;
    }

    public CloseableHttpResponse getStatusFromDeleteRequest(String url) throws IOException, URISyntaxException {
        var response = getStatusAfterSendingDeleteRequest(url);
        response.close();
        return response;
    }

    private CloseableHttpResponse getStatusAfterSendingPostRequest(String url) throws URISyntaxException, IOException {
        var post = new HttpPost(new URI(url));
        post.addHeader(bearerHeader);
        return httpClient.execute(post, httpContext);
    }

    private CloseableHttpResponse getStatusAfterSendingGetRequest(String url) throws URISyntaxException, IOException {
        var get = new HttpGet(new URI(url));
        get.addHeader(bearerHeader);
        return httpClient.execute(get, httpContext);
    }

    private CloseableHttpResponse getStatusAfterSendingDeleteRequest(String url) throws URISyntaxException, IOException {
        var delete = new HttpDelete(new URI(url));
        delete.addHeader(bearerHeader);
        return httpClient.execute(delete, httpContext);
    }
}

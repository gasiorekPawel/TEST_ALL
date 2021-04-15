import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static helpers.TestConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseTests extends TestBase {

    String anyCategoryID;

    private Stream<Arguments> goodUrls() {
        return Stream.of(Arguments.of(apiHelper.getCategoryByIDURL("709")),
                Arguments.of(apiHelper.getCategoryParametersByIDURL("709")),
                Arguments.of(apiHelper.getGetCategoriesURL()));
    }

    private Stream<Arguments> badUrls() {
        return Stream.of(Arguments.of(apiHelper.getCategoryByIDURL("6666999439")),
                Arguments.of(apiHelper.getCategoryParametersByIDURL("6666994399")));
    }

    @BeforeAll
    void findAnyCategoryID() throws IOException, URISyntaxException {
        anyCategoryID = apiHelper.getListing().stream().findAny().orElseThrow().get(ID).toString();
    }

    @Order(1)
    @MethodSource("goodUrls")
    @ParameterizedTest
    void sendGetRequestAndVerifyResponse(String url) throws IOException, URISyntaxException {
        var response = apiHelper.getStatusFromGetRequest(url);
        Assertions.assertAll(
                () -> assertEquals(200, response.getStatusLine().getStatusCode(), "Wrong response after sending GET request"),
                () -> assertEquals("application/vnd.allegro.public.v1+json;charset=UTF-8", response.getEntity().getContentType().getValue(), "Wrong content-type")
        );
    }

    @Order(2)
    @MethodSource("goodUrls")
    @ParameterizedTest
    void sendPostRequestAndVerifyResponse(String url) throws IOException, URISyntaxException {
        var response = apiHelper.getStatusFromPostRequest(url);
        Assertions.assertAll(
                () -> assertEquals(405, response.getStatusLine().getStatusCode(), "Wrong response after sending POST request"),
                () -> assertEquals("application/json; charset=utf-8", response.getEntity().getContentType().getValue(), "Wrong content-type")
        );
    }

    @Order(3)
    @MethodSource("goodUrls")
    @ParameterizedTest
    void sendDeleteRequestAndVerifyResponse(String url) throws IOException, URISyntaxException {
        var response = apiHelper.getStatusFromDeleteRequest(url);
        Assertions.assertAll(
                () -> assertEquals(405, response.getStatusLine().getStatusCode(), "Wrong response after sending DELETE request"),
                () -> assertEquals("application/json; charset=utf-8", response.getEntity().getContentType().getValue(), "Wrong content-type"));
    }

    @Order(4)
    @MethodSource("badUrls")
    @ParameterizedTest
    void sentGetRequestWithWrongParameter(String url) throws IOException, URISyntaxException {
        var response = apiHelper.getStatusFromGetRequest(url);
        Assertions.assertAll(
                () -> assertEquals(404, apiHelper.getStatusFromGetRequest(url).getStatusLine().getStatusCode(), "Wrong response after sending GET request with wrong parameter"),
                () -> assertEquals("application/vnd.allegro.public.v1+json;charset=UTF-8", response.getEntity().getContentType().getValue(), "Wrong content-type"));
    }

    @Order(5)
    @Test
    void verifyGetCategoryByIDJSONStructure() throws IOException, URISyntaxException {
        assertTrue(apiHelper.verifyGetCategoryByIdResponseContent(anyCategoryID));
    }

    @Order(6)
    @Test
    void verifyGetCategoryParametersJSONStructure() throws IOException, URISyntaxException {
        assertTrue(apiHelper.verifyGetCategoryParametersResponseContent(anyCategoryID));
    }

    @Order(7)
    @Test
    void verifyGetMainCategoriesJSONStructure() throws IOException, URISyntaxException {
        assertTrue(apiHelper.verifyGetMainCategoriesResponseContent());
    }
}

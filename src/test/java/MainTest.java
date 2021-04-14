import com.fasterxml.jackson.annotation.JsonTypeInfo;
import helpers.ApiHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MainTest{

    @Autowired
    protected ApiHelper apiHelper;

//    @BeforeAll
//    void setUp() throws IOException, URISyntaxException {
//        apiHelper.sendPostRequestWithCred();
//    }

    @Test
    @Order(1)
    void firstTest() throws IOException, URISyntaxException {
        apiHelper = new ApiHelper();
        apiHelper.setToken();
        var x1main = apiHelper.getListing().get(1);
        var x2main = apiHelper.getListing().get(2);

        var x1 = x1main.get("id").toString();
        var x2 = x2main.get("id").toString();
        var x11 =  apiHelper.getCategoryByID(x1);
        apiHelper.getPostCategoryParametersStatus("709");
        var x22 =  apiHelper.getCategoryByID(x2);
        var x222 = apiHelper.getCategoryParameters(x1);
        var x221 = apiHelper.getCategoryParameters("709");
        assertAll(
                ()->         assertEquals(x1main.get("name"),x11.get("name")),
                ()->         assertEquals(x2main.get("name"),x22.get("name"))
        );
    }

    @Test
    @Order(2)
    void checkParametersResponses1() throws IOException, URISyntaxException {
        assertTrue(apiHelper.getDeleteCategoryParametersStatus("709")==405);
    }

    @Test
    @Order(3)
    void checkParametersResponses2() throws IOException, URISyntaxException {
        assertTrue(apiHelper.getPostCategoryParametersStatus("709")==405);
    }

    @Test
    @Order(4)
    void checkParametersResponses3() throws IOException, URISyntaxException {
        assertTrue(apiHelper.getGetCategoryParametersStatus("709")==200);
    }

    @Test
    @Order(4)
    void checkParametersResponsesWrongID() throws IOException, URISyntaxException {
        assertTrue(apiHelper.getGetCategoryParametersStatus("66669999")==404);
    }
}

import helpers.ApiHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBase {

    @Autowired
    protected ApiHelper apiHelper;

    @BeforeAll
    void authenticate() throws IOException, URISyntaxException {
        apiHelper = new ApiHelper();
        apiHelper.setToken();
    }
}

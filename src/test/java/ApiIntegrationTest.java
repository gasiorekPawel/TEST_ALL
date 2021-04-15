import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static helpers.TestConstants.ID;
import static helpers.TestConstants.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiIntegrationTest extends TestBase {

    private List<Map<String, Object>> categories() throws IOException, URISyntaxException {
        return apiHelper.getListing();
    }

    @Order(1)
    @MethodSource("categories")
    @ParameterizedTest
    void verifyIfCategoryNamesIntegration(Map<String, Object> category) throws IOException, URISyntaxException {
        var id = category.get(ID).toString();
        var categoryById = apiHelper.getCategoryByID(id);
        assertEquals(category.get(NAME), categoryById.get(NAME));
    }
}

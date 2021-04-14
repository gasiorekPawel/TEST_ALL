package entitles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {
    @JsonProperty(value = "access_token")
    private String access_token;
    @JsonProperty(value = "token_type")
    private String token_type;
    @JsonProperty(value = "expires_in")
    private Object expires_in;
    @JsonProperty(value = "scope")
    private Object scope;
    @JsonProperty(value = "allegro_api")
    private Object allegro_api;
    @JsonProperty(value = "jti")
    private Object jti;
}

package endpoint.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import endpoint.backend.Constants;
import endpoint.backend.model.Challenge;

/**
 * An endpoint class we are exposing
 */
@Api(
    name = "challengeApi",
    version = "v1",
    namespace = @ApiNamespace(
            ownerDomain = Constants.API_OWNER,
            ownerName = Constants.API_OWNER,
            packagePath = Constants.API_PACKAGE_PATH
    )
)
/**
 * Created by bradenhart on 6/09/15.
 */
public class ChallengeEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "postChallenge")
    public void postChallenge() {
        Challenge response = new Challenge();
        response.setData("Hi, " + name);

        return response;
    }
}

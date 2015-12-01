package com.example.bradenhart.myapplication.backend.apis;

import com.example.bradenhart.myapplication.backend.Constants;
import com.example.bradenhart.myapplication.backend.models.Challenge;
import com.example.bradenhart.myapplication.backend.util.EndpointUtil;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.example.bradenhart.myapplication.backend.OfyService.ofy;

/**
 * Exposes REST API over Challenge resources.
 */
@Api(name = "challengeAssistant", version = "v2",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "challenges",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)

/**
 * Created by bradenhart on 4/09/15.
 */
public class ChallengeEndpoint {

    /**
     * Log output.
     */
    private static final Logger LOG =
            Logger.getLogger(ChallengeEndpoint.class.getName());

    /**
     * Lists all the entities inserted in datastore.
     * @param user the user requesting the entities.
     * @return the list of all entities persisted.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @SuppressWarnings({"cast", "unchecked"})
    @ApiMethod(httpMethod = "GET")
    public final List<Challenge> listChallenges(final User user) throws
            ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        return ofy().load().type(Challenge.class).list();
    }

}

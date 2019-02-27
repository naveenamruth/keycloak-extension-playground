package com.github.thomasdarimont.keycloak.auth.simpleform;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Random;

public class SimpleAuthenticatorForm implements Authenticator {

    private static final Logger LOG = Logger.getLogger(SimpleAuthenticatorForm.class);

    public SimpleAuthenticatorForm(KeycloakSession session) {
        // configure from session
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {


        Random random = new Random();

        int x = random.nextInt(5);
        int y = random.nextInt(5);

        context.getAuthenticationSession().setAuthNote("expectedSum", "" + (x + y));

        Response response = context.form()
                .setAttribute("username", context.getUser().getUsername())
                .setAttribute("x", x)
                .setAttribute("y", y)
                .createForm("simple-form.ftl");

        context.challenge(response);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        int expectedSum = Integer.parseInt(context.getAuthenticationSession().getAuthNote("expectedSum"));
        int givenSum = Integer.parseInt(formData.getFirst("givenSum"));

        LOG.infof("Retrieved givenSum=%s expectedSum=%s", givenSum, expectedSum);

        if (givenSum == expectedSum) {
            context.success();
            return;
        }

        context.failure(AuthenticationFlowError.INTERNAL_ERROR);
    }

    @Override
    public void close() {
        // NOOP
    }
}

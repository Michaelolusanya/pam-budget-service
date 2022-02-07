package ikea.imc.pam.budget.service.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import ikea.imc.pam.budget.service.util.ApplicationContextUtil;
import org.apache.logging.log4j.Logger;
import ikea.imc.pam.budget.service.configuration.properties.OAuthProperties;
import org.json.JSONObject;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import org.apache.logging.log4j.LogManager;

public class JWTVerifier {
    private static final Logger logger = LogManager.getLogger(JWTVerifier.class);

    public boolean isJwtVerified(String authHeader) {
        String microsoftKeysUrl = "https://login.microsoftonline.com/common/discovery/keys";
        try {
            String aadToken = authHeader.split(" ")[1];
            DecodedJWT jwt = JWT.decode(aadToken);
            if (isCorrectIssuer(jwt) && isCorrectAppId(jwt)) {

                JwkProvider provider = new UrlJwkProvider(new URL(microsoftKeysUrl));
                Jwk jwk = provider.get(jwt.getKeyId());
                Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
                // if the token signature is invalid, the method will throw
                // SignatureVerificationException
                algorithm.verify(jwt);
                return true;
            }
        } catch (Exception ex) {
            logger.error("Error message: " + ex.getMessage());
        }
        return false;
    }

    public boolean isCorrectIssuer(DecodedJWT jwt) {
        OAuthProperties oAuthProperties = ApplicationContextUtil.getBean(OAuthProperties.class);
        Object allowedIssuer =
                "https://sts.windows.net/" + oAuthProperties.getMicrosoft().getTenantId() + "/";
        boolean correctIssuer = (jwt.getIssuer() != null && jwt.getIssuer().equals(allowedIssuer));
        if (correctIssuer == false) {
            logger.error("Incorrect Issuer: " + jwt.getIssuer());
        }
        return correctIssuer;
    }

    public boolean isCorrectAppId(DecodedJWT jwt) {
        OAuthProperties oAuthProperties = ApplicationContextUtil.getBean(OAuthProperties.class);
        JSONObject json = getJSONObjectFromJWT(jwt.getToken());
        String appid = ((json == null) ? null : json.getString("appid"));
        boolean correctAppId =
                (appid != null && appid.equals(oAuthProperties.getClientScope().getId()));
        if (correctAppId == false) {
            logger.warn("Incorrect appid (client id within the token): " + appid);
        }
        return correctAppId;
    }

    private JSONObject getJSONObjectFromJWT(String token) {
        String[] chunks = token.split("\\.");
        String jsonString = new String(Base64.getDecoder().decode(chunks[1]));
        JSONObject json = new JSONObject(jsonString);
        return json;
    }
}

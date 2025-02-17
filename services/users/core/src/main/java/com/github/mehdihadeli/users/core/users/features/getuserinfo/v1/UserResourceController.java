package com.github.mehdihadeli.users.core.users.features.getuserinfo.v1;

import com.github.mehdihadeli.buildingblocks.security.OAuthRestTemplate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ref:
// https://github.com/thomasdarimont/keycloak-project-example/blob/main/apps/bff-springboot3/src/main/java/com/github/thomasdarimont/apps/bff3/api/UsersResource.java

@RestController
@RequestMapping("/api/v1/users")
class UserResourceController {

    private final OAuthRestTemplate oauthRestTemplate;

    public UserResourceController(OAuthRestTemplate oauthRestTemplate) {
        this.oauthRestTemplate = oauthRestTemplate;
    }

    @GetMapping("/me")
    public ResponseEntity<Object> userInfo(Authentication auth) {
        //        var userInfo = getUserInfoFromAuthority(auth);
        var userInfo = getUserInfoFromRemote(auth);
        return ResponseEntity.ok(userInfo);
    }

    private Map<String, Object> getUserInfoFromAuthority(Authentication auth) {
        return auth.getAuthorities().stream() //
                .filter(OidcUserAuthority.class::isInstance) //
                .map(authority -> (OidcUserAuthority) authority) //
                .map(OidcUserAuthority::getUserInfo) //
                .map(OidcUserInfo::getClaims) //
                .findFirst() //
                .orElseGet(() -> Map.of("error", "UserInfoMissing"));
    }

    private UserInfo getUserInfoFromRemote(Authentication auth) {
        var principal = (DefaultOidcUser) auth.getPrincipal();
        var idToken = principal.getIdToken();
        var issuerUri = idToken.getIssuer().toString();
        return oauthRestTemplate
                .oauthRequestTokenRestTemplate()
                .getForObject(issuerUri + "/protocol/openid-connect/userinfo", UserInfo.class);
    }

    static class UserInfo extends LinkedHashMap<String, Object> {}
}

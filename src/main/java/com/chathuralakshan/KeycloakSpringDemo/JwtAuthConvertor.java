package com.chathuralakshan.KeycloakSpringDemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//to add "ROLE_" prefix to the jwt decoded role so that spring understand it
@Component
public class JwtAuthConvertor implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${app.client_id}")
    private String client_id;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();

    @Value("${app.principleAttribute}")
    private String principleAttribute;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Collection<GrantedAuthority> authorities= Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(source).stream(),
                extractResourceRoles(source).stream()
        ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(
                source,
                authorities,
                getPrincipleClaimName(source)
        );
    }

    private String getPrincipleClaimName(Jwt jwt) {
        //if we have configured the prinicple attribute, extract that. else use the default attribute "SUB" in jwt whih usually is ID in keycloak
        String claimName= JwtClaimNames.SUB;
        if(principleAttribute!=null){
            claimName=principleAttribute;
        }

        return jwt.getClaim(claimName);

    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String,Object> resourceAccess;
        Map<String,Object> resource;
        Collection<String> resourceRoles;

        if(jwt.getClaim("resource_access")==null){
            return Set.of();//return empty collection
        }

        resourceAccess=jwt.getClaim("resource_access");

        //checking for client_id name in jwt token
        if(resourceAccess.get(client_id)==null){
            return Set.of();
        }

        resource=(Map<String, Object>) resourceAccess.get(client_id);

        //getting the roles defined
        resourceRoles=(Collection<String>) resource.get("roles");

        //extracting the troles and mapping to new role names with prefix "ROLE_"
        return resourceRoles
                .stream()
                .map(role->new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toSet());

    }
}

# Spring-Boot-Auth-Using-Keycloak (Tutorial - learning)
- The springboot rest client is configured for keycloak 

- In application.properties file JWT issuer uri and JWK set uri is defined according to the keycloak auth server

- To use the REST APIs, the request should have a JWT token issued by keycloak server.
- Using a valid email and password, a requesrt can be made to the keycloak server and get a access token.
- Springboot will validate the token with the keycloak server when a request is received and authorize according to the role defined in the JWT token

### A custom JWT converter is implemented to add the prefix "ROLE_" to the roles defined in the jwt since spring security looks for the prefix.

- Keycloak server is not available in the repo, Use keycloak docker or installed version and change the issuer_uri, jwk_set_uri and client_id according to the new configuration
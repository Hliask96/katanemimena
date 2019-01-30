package com.example.front.classes;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider
        implements AuthenticationProvider {

    HttpHeaders createHeaders(String email, String password) {
        HttpHeaders acceptHeaders = new HttpHeaders() {
            {
                set(com.google.common.net.HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON.toString());
            }
        };
        String authorization = email + ":" + password;
        String basic = new String(Base64.encodeBase64
                (authorization.getBytes(Charset.forName("US-ASCII"))));
        acceptHeaders.set("Authorization", "Basic " + basic);

        return acceptHeaders;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
//        System.out.println(name);
//        System.out.println(password);


        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Principal> entity = new HttpEntity<Principal>(createHeaders(name, password));
//        System.out.println(entity);


        try {
            ResponseEntity<User> a = restTemplate.exchange("http://localhost:8080/api/me", HttpMethod.GET, entity, User.class);
            System.out.println(a);

        } catch (HttpStatusCodeException exception) {

            System.out.println(exception);
            return null;
        }

        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(name, password));


//        if (name.equals("admin") && password.equals("system")) {
  //          final List<GrantedAuthority> grantedAuths = new ArrayList<>();
    //        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
//            final UserDetails principal = new User(name, password, grantedAuths);
        final Authentication auth = new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());

        return auth;
//        } else {
//            return null;
//        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}

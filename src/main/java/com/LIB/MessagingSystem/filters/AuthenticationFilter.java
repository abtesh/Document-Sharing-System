package com.LIB.MessagingSystem.filters;

import com.LIB.MessagingSystem.Dto.SecurityDtos.LdapUserDTO;
import com.LIB.MessagingSystem.Service.Impl.JwtService;
import com.LIB.MessagingSystem.Service.Impl.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final LdapTemplate ldapTemplate;
    private final JwtService jwtService;
    private final UsersService usersService;
    private final String searchBase;


    public AuthenticationFilter(AuthenticationManager authenticationManager, LdapTemplate ldapTemplate, JwtService jwtService, UsersService usersService, String searchBase, ObjectMapper objectMapper) {
        super("/api/login");
        this.ldapTemplate = ldapTemplate;
        this.jwtService = jwtService;
        this.usersService = usersService;
        this.searchBase = searchBase;
        this.objectMapper = objectMapper;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), HashMap.class);
        String email = credentials.get("email");
        String password = credentials.get("password");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        LdapUserDTO user = getUserObjectGUIDByEmail(userDetails.getUsername());
        usersService.saveUsers(user.getEmail(),user.getName(),user.getUid());
        var jwtToken = jwtService.generateToken(userDetails, user);
        response.setContentType("application/json");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Authentication successful!");
        responseBody.put("isSuccessful", true);
        responseBody.put("statusCode", 200);
        responseBody.put("data", jwtToken);
        response.setStatus(HttpServletResponse.SC_OK);
        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.error("Authentication failed: {}", failed.toString());
        response.setContentType("application/json");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Authentication successful!");
        responseBody.put("isSuccessful", false);
        responseBody.put("statusCode", 401);
        responseBody.put("data","Authentication failed: " + failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }


    public LdapUserDTO getUserObjectGUIDByEmail(String email) {
        EqualsFilter filter = new EqualsFilter("userPrincipalName", email);

        List<LdapUserDTO> users = ldapTemplate.search(searchBase, filter.encode(), new AttributesMapper<LdapUserDTO>() {
            @Override
            public LdapUserDTO mapFromAttributes(Attributes attributes) throws NamingException {
                byte[] objectGuidBytes = (byte[]) attributes.get("objectGUID").get();
                String objectGUID = convertBytesToGUID(objectGuidBytes);
                String username = (String) attributes.get("cn").get();
                return LdapUserDTO.builder().email(email).name(username).uid(objectGUID).build();
            }
        });

        // Assuming only one result should match the email
        return users.isEmpty() ? null : users.get(0);
    }

    private String convertBytesToGUID(byte[] guidBytes) {
        UUID uuid = UUID.fromString(
                String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
                        guidBytes[3] & 255,
                        guidBytes[2] & 255,
                        guidBytes[1] & 255,
                        guidBytes[0] & 255,
                        guidBytes[5] & 255,
                        guidBytes[4] & 255,
                        guidBytes[7] & 255,
                        guidBytes[6] & 255,
                        guidBytes[8] & 255,
                        guidBytes[9] & 255,
                        guidBytes[10] & 255,
                        guidBytes[11] & 255,
                        guidBytes[12] & 255,
                        guidBytes[13] & 255,
                        guidBytes[14] & 255,
                        guidBytes[15] & 255));
        log.info(uuid.toString());
        return uuid.toString();
    }

}

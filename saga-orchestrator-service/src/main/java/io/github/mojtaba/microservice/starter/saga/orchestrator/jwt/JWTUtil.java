package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;

import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.BackofficeOrchestratorException;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.BusinessExceptionCode;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.SecurityConfigurationException;
import io.github.mojtaba.microservice.starter.saga.orchestrator.model.UploadDocumentTokenData;
import io.github.mojtaba.microservice.starter.shared.model.enums.Platform;
import io.github.mojtaba.microservice.starter.shared.model.profile.RegisterToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

@Component
@Slf4j
public class JWTUtil implements InitializingBean {
    private final static String TOKEN_PREFIX = "Bearer ";

    @Value("${jwtKey}")
    private String secret;

    @Value("${jwtExpiration}")
    private Long expiration;
    private static final String SCHEDULE_ROLE = "SCHEDULE";
    private static final String SCHEDULER_CDN_PERMISSION = "CDN_EKYC_DOCUMENT_UPLOAD";

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Date getCreatedDateFromToken(String token) {
        try {
            final Claims claims = getClaimsFromToken(token);
            return new Date((Long) claims.get(Claims.ISSUED_AT));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(BusinessExceptionCode.JWT_PARSE_EXCEPTION.name(), e);
        }
    }

    private Claims getClaimsFromToken(String token) {
        try {
            String extractedToken = extractTokenData(token);
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(extractedToken)
                    .getPayload();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackofficeOrchestratorException(BusinessExceptionCode.JWT_PARSE_EXCEPTION);
        }
    }

    public JwtDataDto getJwtDataDtoFromToken(String token) {

        Claims claims = getClaimsFromToken(token);
        JwtDataDto jwt = new JwtDataDto();
        jwt.setCif(claims.get(CustomClaims.CIF, String.class));
        jwt.setLastname("");
        jwt.setCustomerId(claims.getSubject());

        String sRoles = (String) claims.get(CustomClaims.ROLES);
        jwt.setRolesName(convertCommaSeparatedToList(sRoles));

        String permissions = (String) claims.get(CustomClaims.PERMISSIONS);
        jwt.setPermissionName(convertCommaSeparatedToList(permissions));

        jwt.setUsername((String) claims.get(CustomClaims.USERNAME));
        try {
            jwt.setMobile((String) claims.get(CustomClaims.MOBILE));
        } catch (Exception e) {
            jwt.setMobile("");
        }
        try {
            jwt.setUserIP((String) claims.get(CustomClaims.USERIP));
        } catch (Exception e) {
            jwt.setUserIP("Anonymous");
        }
        return jwt;
    }

    public Optional<TokenIssuer> getIssuerFromToken(String token) {
        String iss = getClaimsFromToken(token).get("iss", String.class);
        return TokenIssuer.fromValue(iss);
    }

    public RegisterToken getRegisterTokenObjectFromRegisterTokenString(String token) throws ParseException {

        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(7);
        }
        Claims claims = getClaimsFromToken(token);
        RegisterToken model = new RegisterToken();
        model.setPlatform(Platform.valueOf(claims.get(CustomClaims.PLATFORM).toString()));
        model.setCustomerId(claims.get(Claims.SUBJECT).toString());
        model.setVersion(claims.get(CustomClaims.VERSION).toString());
        model.setDeviceId(claims.get(CustomClaims.DEVICE).toString());
        model.setMobile(claims.get(CustomClaims.MOBILE).toString());
        model.setExpirationDate(new Date(Long.parseLong(claims.get(Claims.EXPIRATION).toString())));
        model.setCreateDate(new Date(Long.parseLong(claims.get(Claims.ISSUED_AT).toString())));
        Object nationalCode = claims.get(RegisterClaims.NATIONAL_CODE);
        if (!StringUtils.isEmpty(nationalCode)) {
            model.setNationalCode((String) nationalCode);
        }
        try {
            model.setCustomerSessionId(claims.get(CustomClaims.CUSTOMERSESSIONID).toString());
        } catch (Exception e) {
            throw new BackofficeOrchestratorException(BusinessExceptionCode.TOKEN_EXPIRED);
        }
        if (model.getExpirationDate().before(new Date())) {
            throw new RuntimeException(BusinessExceptionCode.TOKEN_EXPIRED.name());
        }
        return model;
    }

    private List<String> convertCommaSeparatedToList(String str) {
        String parts[] = StringUtils.commaDelimitedListToStringArray(str);
        if (parts == null || parts.length == 0) {
            return null;
        } else {
            return new ArrayList<>(Arrays.asList(parts));
        }
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Date generateAdvocateExpirationDate() {

        return new Date(System.currentTimeMillis() + 12000000);
    }

    public String generateIamToken(JWTUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, userDetails.getUsername().toLowerCase());
        claims.put(CustomClaims.USERNAME, userDetails.getUsername());
        claims.put(Claims.AUDIENCE, "web");
        claims.put(Claims.ISSUED_AT, new Date());
        claims.put(Claims.ISSUER, TokenIssuer.CITY_DI_SSO_IAM.getValue());
        claims.put(CustomClaims.PERMISSIONS, StringUtils.collectionToCommaDelimitedString(userDetails.getPermissions()));
        claims.put(CustomClaims.ROLES, StringUtils.collectionToCommaDelimitedString(userDetails.getRoles()));
        return generateIamToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .claims(claims)
                .expiration(generateExpirationDate())
                .signWith(getSigningKey())
                .compact();
        return TOKEN_PREFIX + token;
    }

    public String generateUploadDocumentTokenData(UploadDocumentTokenData model) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(UploadDocumentTokenData.USERNAME, "scheduler");
        claims.put(UploadDocumentTokenData.SESSION_EXPIRE_DATE, model.getSessionExpireDate());
        claims.put(CustomClaims.ROLES, SCHEDULE_ROLE);
        claims.put(CustomClaims.PERMISSIONS, SCHEDULER_CDN_PERMISSION);
        return generateTokenWithCustomExpiration(claims, model.getSessionExpireDate());
    }

    public String generateTokenWithCustomExpiration(Map<String, Object> claims, Date expiration) {
        String token = Jwts.builder()
                .claims(claims)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
        return TOKEN_PREFIX + token;
    }

    private String generateIamToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .claims(claims)
                .expiration(generateAdvocateExpirationDate())
                .signWith(getSigningKey())
                .compact();
        return TOKEN_PREFIX + token;
    }

    public String refreshToken(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(7);
        }
        String refreshedToken;
        final Claims claims = getClaimsFromToken(token);
        // In new JJWT, you need to create new claims map instead of modifying existing
        Map<String, Object> newClaims = new HashMap<>(claims);
        newClaims.put(Claims.EXPIRATION, generateExpirationDate());
        refreshedToken = generateToken(newClaims);

        return refreshedToken;
    }

    @Override
    public void afterPropertiesSet() {
        if (org.apache.commons.lang3.StringUtils.isBlank(secret) || secret.length() < 10) {
            throw new SecurityConfigurationException("jwtkey is blank or shorter than 10 characters");
        }
        if (expiration == null || expiration < 60) {
            throw new SecurityConfigurationException("jwtExpiration is null or less than 60 seconds");
        }
    }

    private String extractTokenData(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }
        return token;
    }
}
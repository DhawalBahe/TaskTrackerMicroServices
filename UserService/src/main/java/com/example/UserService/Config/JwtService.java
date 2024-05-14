package com.example.UserService.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCQWEDFGHJUY";

	public String extractUserEmail(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public Boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUserEmail(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
































//mycode
//
//private static final String SECKRET_KEY = "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9";//greater then 256bits
//
//public String extractUserEmail(String Token) {
//	return extractClaim(Token, Claims::getSubject);
//}
//
//public String genrateToken(UserDetails userDetails) {
//	return genrateToken(new HashMap<>(), userDetails);
//}
//
//public boolean isTokenValid(String Token, UserDetails userDetails) {
//	final String userName = extractUserEmail(Token);
//	return (userName.equals(userDetails.getUsername())) && !isTokenExpired(Token);
//
//}
//
//private boolean isTokenExpired(String token) {
//	return extractExpiation(token).before(new Date());
//}
//
//private Date extractExpiation(String token) {
//	return extractClaim(token, Claims::getExpiration);
//}
//
//public String genrateToken(Map<String, Object> extraCalims, UserDetails userDetails) {
//
//	return Jwts.builder().setClaims(extraCalims).setSubject(userDetails.getUsername())
//			.setIssuedAt(new Date(System.currentTimeMillis()))
//			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
//			.signWith(SignatureAlgorithm.HS256, SECKRET_KEY).compact();
//}
//
//public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//	final Claims Claims = extractAllClaims(token);
//	return claimResolver.apply(Claims);
//
//}
//
//private Claims extractAllClaims(String Token) {
//	return Jwts.parserBuilder().setSigningKey(SECKRET_KEY).build().parseClaimsJws(Token).getBody();
//
//}
//
//private SecretKey getsigningKey() {
//	byte[] key = Decoders.BASE64.decode(SECKRET_KEY);
//	return Keys.hmacShaKeyFor(key);
//
//}

package com.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.test.dto.ReqRes;
import com.test.entity.OurUsers;
import com.test.repository.OurUserRepo;

import java.util.HashMap;

@Service
public class AuthService {

    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUp(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setRole(registrationRequest.getRole());
            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
            if (ourUserResult != null && ourUserResult.getId()>0) {
                resp.setOurUsers(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

//    public ReqRes signIn(ReqRes signinRequest){
//        ReqRes response = new ReqRes();
//
//        try {
//        	Authentication authentication = new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword());
//        	authenticationManager.authenticate(authentication);
//            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),signinRequest.getPassword()));
//            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
//            System.out.println("USER IS: "+ user);
//            var jwt = jwtUtils.generateToken(user);
//            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
//            System.out.println("JWT = " + jwt);
//            System.out.println("Refresh Token = " + refreshToken);
//            response.setStatusCode(200);
//            response.setToken(jwt);
//            response.setRefreshToken(refreshToken);
//            response.setExpirationTime("24Hr");
//            response.setMessage("Successfully Signed In");
//            response.setRole(user.getRole());
//        }catch (Exception e){
//            response.setStatusCode(500);
//            response.setError(e.getMessage());
//        }
//        return response;
//    }

    public ReqRes signIn(ReqRes signInRequest, Authentication authentication) {
        ReqRes response = new ReqRes();

        try {
            // Create an Authentication object with the user's email and password
            Authentication auth = new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword());

            // Authenticate the user using the Authentication object
            Authentication authenticatedUser = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            
            // Get the authenticated user from the Authentication object
            var user = ourUserRepo.findByEmail(signInRequest.getEmail()).orElseThrow();

            // Generate a JWT token and a refresh token for the user
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            // Set the authentication object in the SecurityContextHolder
            

            // Set the response properties
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
            response.setRole(user.getRole());
        } catch (Exception e) {
            // Set the error message in the response
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }

        return response;
    }

    
//    public ReqRes signIn(ReqRes signInRequest, Authentication authentication) {
//        ReqRes response = new ReqRes();
//
//        try {
//            // Create an Authentication object with the user's email and password
//            Authentication auth = new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword());
//
//            // Authenticate the user using the Authentication object
//            Authentication authenticatedUser = authenticationManager.authenticate(auth);
//
//            // Get the authenticated user from the Authentication object
//            var user = authenticatedUser.getPrincipal();
//
//            // Generate a JWT token and a refresh token for the user
//            var jwt = jwtUtils.generateToken(user);
//            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
//
//            // Set the response properties
//            response.setStatusCode(200);
//            response.setToken(jwt);
//            response.setRefreshToken(refreshToken);
//            response.setExpirationTime("24Hr");
//            response.setMessage("Successfully Signed In");
//            response.setRole(user.getRole());
//        } catch (Exception e) {
//            // Set the error message in the response
//            response.setStatusCode(500);
//            response.setError(e.getMessage());
//        }
//
//        return response;
//    }

    
    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        OurUsers users = ourUserRepo.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}

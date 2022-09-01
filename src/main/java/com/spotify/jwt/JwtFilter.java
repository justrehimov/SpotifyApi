package com.spotify.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.dto.response.ResponseModel;
import com.spotify.entity.User;
import com.spotify.exception.SpotifyException;
import com.spotify.exception.StatusMessage;
import com.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       if(request.getServletPath().contains("/api/auth") || request.getServletPath().startsWith("/api/storage/download")) {
           filterChain.doFilter(request, response);
       }else{
           String header = request.getHeader(HttpHeaders.AUTHORIZATION);
           String username = null;
           String token = null;

           if (header != null && header.contains("Bearer ")) {
               token = header.substring(7);
               if (!jwtService.isValidToken(token)){
                   throw new SpotifyException(StatusMessage.ACCESS_TOKEN_IS_NOT_VALID);
               }
               username = jwtService.getUsernameToken(token);
               User user = userService.getByUsername(username);
               UsernamePasswordAuthenticationToken authenticationToken =
                       new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
               SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               filterChain.doFilter(request, response);
           } else {
               ResponseModel<Object> responseModel = ResponseModel.builder()
                       .message(StatusMessage.ACCESS_TOKEN_IS_NOT_VALID)
                       .error(true)
                       .build();
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               new ObjectMapper().writeValue(response.getOutputStream(),responseModel);
           }
       }
    }
}

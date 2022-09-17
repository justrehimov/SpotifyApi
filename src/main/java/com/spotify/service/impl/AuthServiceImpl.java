package com.spotify.service.impl;

import com.spotify.dto.request.LoginRequest;
import com.spotify.dto.request.ResetPasswordRequest;
import com.spotify.dto.request.UserRequest;
import com.spotify.dto.response.LoginResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;
import com.spotify.entity.ConfirmToken;
import com.spotify.entity.User;
import com.spotify.exception.SpotifyException;
import com.spotify.exception.StatusMessage;
import com.spotify.jwt.JwtService;
import com.spotify.repo.ConfirmTokenRepo;
import com.spotify.repo.UserRepo;
import com.spotify.service.AuthService;
import com.spotify.service.ConfirmTokenService;
import com.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmTokenRepo confirmTokenRepo;
    private final ConfirmTokenService confirmTokenService;
    private final UserService userService;
    private final JavaMailSender javaMailSender;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${front.user.confirm.url}")
    private String frontConfirmUrl;

    @Value(("${front.user.reset.password.url}"))
    private String frontPasswordResetUrl;

    @Override
    @Transactional
    public ResponseModel<UserResponse> signUp(UserRequest userRequest) {
        try{
            if(userRepo.existsByEmailOrUsername(userRequest.getEmail(), userRequest.getUsername())){
                throw new SpotifyException(StatusMessage.DUPLICATE_USER);
            }
            User user = modelMapper.map(userRequest, User.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepo.save(user);
            sendConfirmMail(savedUser);
            UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);

            return ResponseModel.<UserResponse>builder()
                    .result(userResponse)
                    .error(false)
                    .message(StatusMessage.SUCCESS)
                    .build();

        }catch (SpotifyException ex){
            log.error("Error ", ex);
            return ResponseModel.<UserResponse>builder()
                    .error(true)
                    .message(ex.getMessage())
                    .build();
        }catch (Exception ex){
            log.error("Error ", ex);
            return ResponseModel.<UserResponse>builder()
                    .error(true)
                    .message(ex.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseModel<LoginResponse> login(LoginRequest loginRequest) {
        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            User user = (User)authentication.getPrincipal();
            String accessToken = jwtService.generateToken(user);
            LoginResponse loginResponse = new LoginResponse(user.getId(), accessToken);

            return ResponseModel.<LoginResponse>builder()
                    .result(loginResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();
        }catch (SpotifyException ex){
            return ResponseModel.<LoginResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (AuthenticationException ex){
            return ResponseModel.<LoginResponse>builder()
                    .message(StatusMessage.INVALID_CREDENTIALS)
                    .error(true)
                    .build();
        }
        catch (Exception ex){
            return ResponseModel.<LoginResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseModel<UserResponse> confirm(String token) {
        try{
            ConfirmToken confirmToken = confirmTokenService.getByToken(token);
            if(!confirmTokenService.isValidToken(token)){
                return ResponseModel.<UserResponse>builder()
                        .message(StatusMessage.CONFIRM_TOKEN_HAS_EXPIRED)
                        .error(true)
                        .build();
            }
            User user = confirmToken.getUser();
            user.setEnabled(true);
            user.setEmail(confirmToken.getEmail());
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            confirmTokenRepo.delete(confirmToken);
            return ResponseModel.<UserResponse>builder()
                    .result(userResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();

        }catch (SpotifyException ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    @Override
    public ResponseModel<UserResponse> forgotPassword(String email) {
        try{
            User user = userService.getByEmail(email);
            sendResetMail(user);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return ResponseModel.<UserResponse>builder()
                    .result(userResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();

        }catch (SpotifyException ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseModel<UserResponse> resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        try{
            if(!confirmTokenService.isValidToken(token)) {
                throw new SpotifyException(StatusMessage.CONFIRM_TOKEN_HAS_EXPIRED);
            }if(!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())){
                throw new SpotifyException(StatusMessage.PASSWORDS_CANNOT_CONTAINS);
            }

            ConfirmToken confirmToken = confirmTokenService.getByToken(token);
            User user = confirmToken.getUser();
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            confirmTokenRepo.delete(confirmToken);
            return ResponseModel.<UserResponse>builder()
                    .result(userResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();

        }catch (SpotifyException ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<UserResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }


    @Async
    protected void sendConfirmMail(User user) throws Exception{
        ConfirmToken confirmToken = new ConfirmToken(user);
        confirmTokenRepo.save(confirmToken);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        String link = frontConfirmUrl + "?token=" + confirmToken.getToken();
        helper.setFrom("desofme@gmail.com");
        helper.setSubject("Confirm mail");
        helper.setTo(user.getEmail());
        helper.setText(getConfirmMailText(link, user.getUsername()), true);
        javaMailSender.send(message);
    }

    @Async
    protected void sendResetMail(User user) throws Exception{
        ConfirmToken confirmToken = new ConfirmToken(user);
        confirmTokenRepo.save(confirmToken);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        String link = frontPasswordResetUrl + "?token=" + confirmToken.getToken();
        helper.setFrom("desofme@gmail.com");
        helper.setSubject("Confirm mail");
        helper.setTo(user.getEmail());
        helper.setText(getResetMailText(link, user.getUsername()), true);
        javaMailSender.send(message);
    };

    private String getConfirmMailText(String confirmLink, String username){
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "       <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Montserrat:wght@300&display=swap\" rel=\"stylesheet\">\n" +
                "    <link rel='stylesheet' media='screen and (max-device-width: 720px)' href='css/home.css' type='text/css' />\n" +
                "    <link rel='stylesheet' media='screen and (max-device-width: 720px)' href='css/music-bar.css' type='text/css' />\n" +
                "   \n" +
                "   <style>\n" +
                "       *{\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    box-sizing: border-box;\n" +
                "    outline: none;\n" +
                "    font-family: 'Montserrat', sans-serif;\n" +
                "}\n" +
                "\n" +
                "body{\n" +
                "    display: flex;\n" +
                "    flex-direction:column;\n" +
                "    background-color: rgb(10, 10, 10);\n" +
                "    z-index: 1;\n" +
                "}\n" +
                "\n" +
                "h3{\n" +
                "    color: white;\n" +
                "    padding:1rem;\n" +
                "}\n" +
                "\n" +
                "p{\n" +
                "    color:white;\n" +
                "    padding:1rem;\n" +
                "}\n" +
                "\n" +
                ".btn-confirm{\n" +
                "    display:flex;\n" +
                "    justify-content:center;\n" +
                "    align-items:center;\n" +
                "    text-decoration:none;\n" +
                "    width:auto;\n" +
                "    height:auto;\n" +
                "    color:white;\n" +
                "    padding:1rem 3rem;\n" +
                "    outline:none;\n" +
                "    border:none;\n" +
                "    border-radius:3rem;\n" +
                "    font-weight:bold;\n" +
                "    font-size:1.2rem;\n" +
                "    margin:auto;\n" +
                "    margin-top:3rem;\n" +
                "    background: rgb(34, 175, 53);\n" +
                "}\n" +
                "   </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h3>Hi, "+username+"</h3>\n" +
                "<p>Click the buton and confirm your email address. Don't forget the enjoy listening musics</p>\n" +
                "\n" +
                "<a class='btn-confirm' href='" + confirmLink + "'>Confirm</a>\n" +
                "</body>\n" +
                "</html>";

        System.out.println(text);
        return text;
    }

    private String getResetMailText(String confirmLink, String username){
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "       <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Montserrat:wght@300&display=swap\" rel=\"stylesheet\">\n" +
                "    <link rel='stylesheet' media='screen and (max-device-width: 720px)' href='css/home.css' type='text/css' />\n" +
                "    <link rel='stylesheet' media='screen and (max-device-width: 720px)' href='css/music-bar.css' type='text/css' />\n" +
                "   \n" +
                "   <style>\n" +
                "       *{\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    box-sizing: border-box;\n" +
                "    outline: none;\n" +
                "    font-family: 'Montserrat', sans-serif;\n" +
                "}\n" +
                "\n" +
                "body{\n" +
                "    display: flex;\n" +
                "    flex-direction:column;\n" +
                "    background-color: rgb(10, 10, 10);\n" +
                "    z-index: 1;\n" +
                "}\n" +
                "\n" +
                "h3{\n" +
                "    color: white;\n" +
                "    padding:1rem;\n" +
                "}\n" +
                "\n" +
                "p{\n" +
                "    color:white;\n" +
                "    padding:1rem;\n" +
                "}\n" +
                "\n" +
                ".btn-confirm{\n" +
                "    display:flex;\n" +
                "    justify-content:center;\n" +
                "    align-items:center;\n" +
                "    text-decoration:none;\n" +
                "    width:auto;\n" +
                "    height:auto;\n" +
                "    color:white;\n" +
                "    padding:1rem 3rem;\n" +
                "    outline:none;\n" +
                "    border:none;\n" +
                "    border-radius:3rem;\n" +
                "    font-weight:bold;\n" +
                "    font-size:1.2rem;\n" +
                "    margin:auto;\n" +
                "    margin-top:3rem;\n" +
                "    background: rgb(34, 175, 53);\n" +
                "}\n" +
                "   </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h3>Hi, "+username+"</h3>\n" +
                "<p>Click the buton and confirm your email address. Don't forget the enjoy listening musics</p>\n" +
                "\n" +
                "<a class='btn-confirm' href='" + confirmLink + "'>Confirm</a>\n" +
                "</body>\n" +
                "</html>";

        return text;
    }
}

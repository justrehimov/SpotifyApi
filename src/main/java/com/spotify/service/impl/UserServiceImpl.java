package com.spotify.service.impl;

import com.spotify.dto.request.UserUpdateRequest;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;
import com.spotify.entity.ConfirmToken;
import com.spotify.entity.User;
import com.spotify.exception.SpotifyException;
import com.spotify.exception.StatusMessage;
import com.spotify.repo.ConfirmTokenRepo;
import com.spotify.repo.UserRepo;
import com.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final JavaMailSender javaMailSender;
    private final ConfirmTokenRepo confirmTokenRepo;

    @Value("${front.user.confirm.url}")
    private String frontConfirmMailUrl;

    @Override
    public User getByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(()->new SpotifyException(StatusMessage.USER_NOT_FOUND));
    }

    @Override
    public User getByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(()->new SpotifyException(StatusMessage.USER_NOT_FOUND));
    }

    @Override
    public User getById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(()->new SpotifyException(StatusMessage.USER_NOT_FOUND));
    }


    @Override
    @Transactional
    public ResponseModel<UserResponse> update(UserUpdateRequest userUpdateRequest, Long id) {
        try{
            User user = getById(id);
            if(userRepo.existsByEmailOrUsername(userUpdateRequest.getEmail(), userUpdateRequest.getUsername())) {
                if(!userUpdateRequest.getEmail().equalsIgnoreCase(user.getEmail()) && !userUpdateRequest.getUsername().equalsIgnoreCase(user.getUsername())) {
                    throw new SpotifyException(StatusMessage.DUPLICATE_USER);
                }
            }if(!user.getEmail().equalsIgnoreCase(userUpdateRequest.getEmail())) {
                ConfirmToken confirmToken = new ConfirmToken(user, userUpdateRequest.getEmail());
                sendConfirmMail(confirmToken, user);
            }
            user.setUsername(userUpdateRequest.getUsername());
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
    public ResponseModel<UserResponse> get(Long id) {
        try{
            User user = getById(id);
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

    @Async
    protected void sendConfirmMail(ConfirmToken confirmToken, User user) throws Exception{

        confirmTokenRepo.save(confirmToken);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        String link = frontConfirmMailUrl + "?token=" + confirmToken.getToken();
        helper.setSubject("Confirm your mail address");
        helper.setFrom(new InternetAddress("desofme@gmail.com", "Spotify"));
        helper.setTo(confirmToken.getEmail());
        helper.setText(getUpdateMailText(link, user.getUsername()), true);
        javaMailSender.send(message);
    }

    private String getUpdateMailText(String confirmLink, String username){
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

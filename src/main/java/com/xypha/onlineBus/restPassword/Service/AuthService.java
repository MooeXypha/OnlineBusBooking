package com.xypha.onlineBus.restPassword.Service;

import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.restPassword.entity.RestToken;
import com.xypha.onlineBus.restPassword.mapper.ResetTokenMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

@Autowired
    private UserMapper userMapper;



@Autowired
    private ResetTokenMapper resetTokenMapper;

@Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> forgotPassword(String email) throws MessagingException {
        User user = userMapper.findByEmail(email);
        if (user == null) {
        return ResponseEntity.status(404).body("Email not found");
        }
        String token = UUID.randomUUID().toString(); //Generate token
        RestToken resetToken = new RestToken();       //save token in DB
        resetToken.setToken(token);
        resetToken.setUserId(user.getId());
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        resetTokenMapper.insertToken(resetToken);

        //Email Service
//        System.out.println("Rest link : http://localhost:4200/reset?token= "+ token);
        String frontendPage = "http://localhost:4200/forgot-password.html";
        String resetLink = frontendPage + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        return ResponseEntity.ok("Password reset link sent to email");
    }


    public ResponseEntity<?> resetPassword (String token, String newPassword){
        RestToken rt = resetTokenMapper.findByToken(token);

        if (rt == null){
            return ResponseEntity.status(400).body("Invalid token");

        }
        if (rt.getExpiryDate().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(400).body("Token expired");
        }

        String encoded = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(rt.getUserId(),encoded);

        resetTokenMapper.deleteByToken(token);

        //fetch user email
        User user = userMapper.getUserById(rt.getUserId());
        Map<String, String> response = new HashMap<>();
        response.put("email", user.getGmail());
        response.put("newPassword", newPassword);

        return ResponseEntity.ok(response);

        //old token expire
//        resetTokenMapper.deleteByToken(String.valueOf(LocalDateTime.now().plusMinutes(10)));
//
//        return ResponseEntity.ok("Password reset successful");
    }

   public List<RestToken> getAllResetToken(){
        return resetTokenMapper.getAllRestTokens();
   }

}

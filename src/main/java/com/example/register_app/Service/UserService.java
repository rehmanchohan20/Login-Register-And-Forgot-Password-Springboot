package com.example.register_app.Service;


import com.example.register_app.Dto.LoginDto;
import com.example.register_app.Dto.RegisterDto;
import com.example.register_app.Repository.UserRepository;
import com.example.register_app.Util.EmailUtil;
import com.example.register_app.Util.OtpUtil;
import com.example.register_app.Entity.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserRepository userRepository;

    public String register(RegisterDto registerDto) {

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(registerDto.getEmail(),otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp try again later");
        }
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());

        userRepository.save(user);

        return "User registered successfully";
    }

    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found with this email" + email));
        if(user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (1 * 60)) {
            user.setActive(true);
            userRepository.save(user);
            return "OTP Verified successfully";
        }
        return "OTP not verified, Regenerate OTP";
    }

    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not sound with this email" + email));
        String otp = otpUtil.generateOtp();

        try {
            emailUtil.sendOtpEmail(email,otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp try again later");
        }

        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        return "OTP Generated Successfully. Please Verify Your Account in 1 Minute";
    }

    public String login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(()-> new RuntimeException("User not sound with this email" + loginDto.getEmail()));

        if(!loginDto.getPassword().equals(user.getPassword())) {
            return "Wrong password";
        } else if (!user.isActive()) {
            return "User not active, your account is not verified";
        }

        return "Login Successfully";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found with this email" + email));
        try {
            emailUtil.sendSetPasswordEmail(email);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to change password try again later");
        }
        return "Please check email to setup new password";

    }

    public String setPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found with this email" + email));
        user.setPassword(newPassword);
        userRepository.save(user);

        return "New Password Set Successfully, login with new password";
    }
}

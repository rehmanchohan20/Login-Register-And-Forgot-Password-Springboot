package com.example.register_app.Util;


import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {
    public String generateOtp() {
        Random random = new Random();
        int randomInt = random.nextInt(999999);
        String output = Integer.toString(randomInt);

        while (output.length() < 6) {
            output = "0" + output;
        }
        return output;

    }
}

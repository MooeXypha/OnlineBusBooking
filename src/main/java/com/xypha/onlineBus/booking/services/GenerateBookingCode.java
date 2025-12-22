package com.xypha.onlineBus.booking.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public final class GenerateBookingCode {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int RANDOM_LENGTH = 6;


    public static String generate(){
        String dataPart = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE
                );
        String randomPart = randomString(RANDOM_LENGTH);
        return "COZYBK-" + dataPart + "-" + randomPart;
    }

    private static String randomString (int length){
        StringBuilder sb= new StringBuilder(length);
        for (int i=0; i<length; i++){
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }


}

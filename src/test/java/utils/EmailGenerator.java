package utils;

import java.util.Random;

public class EmailGenerator {

    public static String generateRandomEmail() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder email = new StringBuilder();
        Random rnd = new Random();

        // Generate random part
        int length = 8;  // Length of random part
        for (int i = 0; i < length; i++) {
            email.append(characters.charAt(rnd.nextInt(characters.length())));
        }

        // Append domain
        email.append("@lulademo.com");

        return email.toString();
    }
}

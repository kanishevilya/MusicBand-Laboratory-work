package server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class PasswordHasher {

    private static final String PEPPER = "Secret_Pepper_Volvo_2026_Helios_Crypto_#99!";

    public static String hashPassword(String password, String login) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-224");

            String complexPassword = PEPPER + password + login;

            byte[] bytes = md.digest(complexPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Критическая ошибка безопасности: алгоритм SHA-224 недоступен!", e);
        }
    }
}
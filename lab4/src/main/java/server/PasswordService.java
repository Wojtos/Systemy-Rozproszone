package server;

import java.security.MessageDigest;
import java.util.Random;
import java.util.stream.Stream;

public class PasswordService {
    private static PasswordService instance;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_SIZE = 20;

    public static PasswordService getInstance() {
        if (instance == null) {
            instance = new PasswordService();
        }
        return instance;
    }

    private PasswordService() {

    }

    public String generatePassword() {
        return Stream
                .generate(new Random()::nextInt)
                .map(Math::abs)
                .limit(PASSWORD_SIZE)
                .map((randomInt) -> ALPHA_NUMERIC_STRING.charAt(randomInt % ALPHA_NUMERIC_STRING.length()))
                .map(Object::toString)
                .reduce("", String::concat);

    }

    public String md5(String toEncode) {
        String toReturn = null;
        try {
            byte[] bytesToEncode = toEncode.getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encodedBytes = md5.digest(bytesToEncode);
            toReturn = new String(encodedBytes);
        } catch (Exception e) {

        } finally {
            return toReturn;
        }
    }
}

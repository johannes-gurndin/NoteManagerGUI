package noteblock;


import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

public class User {
    private static HashMap<String, String> usertokens = new HashMap<>();
    /**
     * Checks if the User is registered in the Database. If that
     * is the case, a generated Token will be returned and
     * user + token will be saved in the static Hashmap. In any
     * other case "false" will be returned.
     *
     * @return the generated Token or "false"
     */
    public static String login(String username, String password) {
        String token = "false";
        Connection cnn = DBManager.getDBConnection();
        assert cnn != null;
        try {
            PreparedStatement pstmt = cnn.prepareStatement("SELECT COUNT(*) FROM users WHERE uname=? AND upass=SHA2(?);");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            if (rs.getInt(1) == 1) {
                Random rnd = new Random();
                try {
                    MessageDigest md = MessageDigest.getInstance("md5");
                    String base = rnd.nextInt(1000000) + "" + System.currentTimeMillis();
                    token = DatatypeConverter.printHexBinary(md.digest(base.getBytes())).toUpperCase();
                    usertokens.put(token, username);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                token = "false";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * If the token is connected to a username the method returnes that specific username, else null is
     * returned.
     *
     * @param token the token connected to the username
     * @return the username of connected to the token or null
     */
    public static String check(String token) {
        return usertokens.containsKey(token) ? usertokens.get(token) : null;
    }

    public static String logout(String token) {
        if (usertokens.containsKey(token)) {
            usertokens.remove(token);
            return "true";
        }
        return "false";
    }
}

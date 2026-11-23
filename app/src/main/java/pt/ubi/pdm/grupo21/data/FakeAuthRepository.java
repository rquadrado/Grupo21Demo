package pt.ubi.pdm.grupo21.data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

// Servidor simulado, para a demo funcionar sem Internet
public class FakeAuthRepository implements AuthRepository {

    private static final String DEMO_USER = "aluno";
    private static final String SALT = "grupo21";
    // SHA-256 de salt + palavra-passe
    private static final String DEMO_PASSWORD_HASH =
            "d6c188eebcfd7fc9015b58c2e86038da9832d469f35d3150184c0c52516e45e3";

    @Override
    public String login(String username, String password) {
        byte[] expected = DEMO_PASSWORD_HASH.getBytes(StandardCharsets.UTF_8);
        byte[] actual = sha256Hex(SALT + password).getBytes(StandardCharsets.UTF_8);

        // tempo constante
        boolean passwordOk = MessageDigest.isEqual(expected, actual);

        if (DEMO_USER.equals(username) && passwordOk) {
            return UUID.randomUUID().toString();
        }
        return null;
    }

    private static String sha256Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

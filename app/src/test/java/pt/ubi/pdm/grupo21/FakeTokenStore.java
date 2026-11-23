package pt.ubi.pdm.grupo21;

import pt.ubi.pdm.grupo21.data.TokenStore;

/** TokenStore em memória, para testes na JVM (o Android Keystore só existe no dispositivo). */
public class FakeTokenStore implements TokenStore {

    private String username;
    private String token;

    @Override
    public void save(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void clear() {
        username = null;
        token = null;
    }
}

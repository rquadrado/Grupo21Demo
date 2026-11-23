package pt.ubi.pdm.grupo21.data;

/**
 * Onde a sessão fica guardada.
 * Na app: KeystoreTokenStore (cifrado com o Android Keystore).
 * Nos testes unitários: uma versão falsa em memória.
 */
public interface TokenStore {

    void save(String username, String token);

    /** @return o token decifrado, ou null se não houver sessão. */
    String getToken();

    String getUsername();

    void clear();
}

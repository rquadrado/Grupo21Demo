package pt.ubi.pdm.grupo21.data;

public interface TokenStore {

    void save(String username, String token);

    // null se não houver sessão
    String getToken();

    String getUsername();

    void clear();
}

package pt.ubi.pdm.grupo21.data;

public interface AuthRepository {

    // devolve null se as credenciais forem inválidas
    String login(String username, String password);
}

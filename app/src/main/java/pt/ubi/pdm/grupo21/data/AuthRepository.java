package pt.ubi.pdm.grupo21.data;

/**
 * Camada de dados: ponto de acesso único à autenticação.
 * O ViewModel só conhece esta interface, não sabe de onde vêm os dados
 * (servidor, Firebase, simulação...). Isto permite trocar a implementação nos testes.
 */
public interface AuthRepository {

    /**
     * @return um token de sessão se as credenciais forem válidas, ou null se forem inválidas.
     */
    String login(String username, String password);
}

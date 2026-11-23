# Excertos equivalentes em Java e Kotlin (Grupo 21)

O enunciado pede pelo menos um excerto na outra linguagem e, quando a tecnologia é sobretudo Kotlin,
a explicação da alternativa em Java. A demonstração está em Java. Os excertos seguintes são para os
diapositivos e não fazem parte do projeto compilado.

---

## 1. Estado no ViewModel: `LiveData` (Java) vs. `StateFlow` (Kotlin)

**Java (código da demo, `ui/LoginViewModel.java`):**

```java
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginUiState> uiState = new MutableLiveData<>();

    public LiveData<LoginUiState> getUiState() {
        return uiState;                       // a View só lê
    }

    public void login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            uiState.setValue(LoginUiState.error(R.string.error_empty_fields));
            return;
        }
        String token = authRepository.login(username.trim(), password);
        if (token == null) {
            uiState.setValue(LoginUiState.error(R.string.error_invalid_credentials));
            return;
        }
        tokenStore.save(username.trim(), token);
        uiState.setValue(LoginUiState.loggedIn(username.trim()));
    }
}
```

**Kotlin (equivalente):**

```kotlin
// Em Kotlin, o estado fica mais expressivo com uma sealed interface
sealed interface LoginUiState {
    data object LoggedOut : LoginUiState
    data class LoggedIn(val username: String) : LoginUiState
    data class Error(@StringRes val messageRes: Int) : LoginUiState
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(
        tokenStore.getToken()?.let { LoginUiState.LoggedIn(tokenStore.getUsername()) }
            ?: LoginUiState.LoggedOut
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()   // a View só lê

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error(R.string.error_empty_fields)
            return
        }
        val token = authRepository.login(username.trim(), password)
        _uiState.value = if (token == null) {
            LoginUiState.Error(R.string.error_invalid_credentials)
        } else {
            tokenStore.save(username.trim(), token)
            LoginUiState.LoggedIn(username.trim())
        }
    }
}
```

**Na Activity:**

```java
// Java: o LiveData já conhece o ciclo de vida
viewModel.getUiState().observe(this, this::render);
```

```kotlin
// Kotlin: o StateFlow é recolhido numa coroutine ligada ao ciclo de vida
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { render(it) }
    }
}
```

**Pontos para dizer:** as duas versões seguem o mesmo fluxo unidirecional (eventos sobem, estado desce).
O `LiveData` é mais simples em Java. O `StateFlow` tem sempre um valor, integra-se com coroutines e é a
opção recomendada em Kotlin, mas obriga a usar `repeatOnLifecycle` para não recolher com o ecrã parado.

---

## 2. Credential Manager: callback (Java) vs. `suspend` (Kotlin)

O Credential Manager é uma API pensada para Kotlin. Em Java, cada função `suspend` tem uma
alternativa `...Async` que recebe um `Executor` e um `CredentialManagerCallback`.

Dependências (`app/build.gradle.kts`, versão estável mais recente):

```kotlin
implementation("androidx.credentials:credentials:<versão>")
implementation("androidx.credentials:credentials-play-services-auth:<versão>") // Android 13 ou inferior
```

### Pedir uma palavra-passe guardada

**Java:**

```java
CredentialManager credentialManager = CredentialManager.create(this);

GetCredentialRequest request = new GetCredentialRequest.Builder()
        .addCredentialOption(new GetPasswordOption())
        .build();

credentialManager.getCredentialAsync(
        this,                                   // Activity
        request,
        new CancellationSignal(),
        ContextCompat.getMainExecutor(this),    // onde corre o callback
        new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onResult(GetCredentialResponse result) {
                Credential credential = result.getCredential();
                if (credential instanceof PasswordCredential) {
                    PasswordCredential pc = (PasswordCredential) credential;
                    viewModel.login(pc.getId(), pc.getPassword());
                }
            }

            @Override
            public void onError(GetCredentialException e) {
                Log.w(TAG, "Nenhuma credencial escolhida", e);
            }
        });
```

**Kotlin:**

```kotlin
val credentialManager = CredentialManager.create(this)

val request = GetCredentialRequest(listOf(GetPasswordOption()))

lifecycleScope.launch {
    try {
        val result = credentialManager.getCredential(this@MainActivity, request)
        val credential = result.credential
        if (credential is PasswordCredential) {
            viewModel.login(credential.id, credential.password)
        }
    } catch (e: GetCredentialException) {
        Log.w(TAG, "Nenhuma credencial escolhida", e)
    }
}
```

### Guardar a palavra-passe depois de um login com sucesso

```java
// Java
credentialManager.createCredentialAsync(
        this,
        new CreatePasswordRequest(username, password),
        new CancellationSignal(),
        ContextCompat.getMainExecutor(this),
        new CredentialManagerCallback<CreateCredentialResponse, CreateCredentialException>() {
            @Override public void onResult(CreateCredentialResponse r) { /* guardada */ }
            @Override public void onError(CreateCredentialException e) { /* ignorada */ }
        });
```

```kotlin
// Kotlin
lifecycleScope.launch {
    try {
        credentialManager.createCredential(this@MainActivity, CreatePasswordRequest(username, password))
    } catch (e: CreateCredentialException) { /* ignorada */ }
}
```

### Passkeys

Para passkeys troca-se a opção do pedido por
`GetPublicKeyCredentialOption(requestJson)`, em que `requestJson` é um desafio WebAuthn gerado pelo
servidor. É por isso que a demo não usa passkeys: precisam de um servidor de autenticação e de um
domínio associado à app (Digital Asset Links). Com a arquitetura da demo, a mudança fica toda na camada
de dados (`AuthRepository`), sem tocar no ViewModel nem na interface.

**Pontos para dizer:** a lógica é a mesma nas duas linguagens. Em Kotlin, `suspend` e `try/catch`
deixam o código sequencial. Em Java, o resultado chega mais tarde por callback, e é preciso indicar
explicitamente o `Executor` onde ele corre.

---

## 3. Trabalho fora da thread principal: `ExecutorService` (Java) vs. coroutines (Kotlin)

Na demo o login é síncrono porque o servidor é simulado. Com uma API remota seria assim:

```java
// Java
private final ExecutorService executor = Executors.newSingleThreadExecutor();

public void login(String username, String password) {
    executor.execute(() -> {
        String token = authRepository.login(username, password);   // thread de fundo
        uiState.postValue(token == null                            // postValue: vindo de outra thread
                ? LoginUiState.error(R.string.error_invalid_credentials)
                : LoginUiState.loggedIn(username));
    });
}

@Override
protected void onCleared() {
    executor.shutdown();   // libertar a thread quando o ViewModel é destruído
}
```

```kotlin
// Kotlin
fun login(username: String, password: String) {
    viewModelScope.launch {
        val token = withContext(Dispatchers.IO) { authRepository.login(username, password) }
        _uiState.value = if (token == null) LoginUiState.Error(R.string.error_invalid_credentials)
                         else LoginUiState.LoggedIn(username)
    }   // cancelada automaticamente quando o ViewModel é destruído
}
```

**Erro frequente a referir:** em Java, chamar `setValue()` fora da thread principal lança uma exceção.
Tem de ser `postValue()`.

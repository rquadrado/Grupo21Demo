# Grupo 21: Arquitetura, segurança, autenticação e testes (demo Android em Java)

Esta app mínima demonstra o que o enunciado pede:

| Requisito | Onde está |
|---|---|
| Autenticação | `data/AuthRepository.java` e `data/FakeAuthRepository.java` (servidor simulado, palavra-passe só em hash) |
| Estado gerido num ViewModel | `ui/LoginViewModel.java` e `ui/LoginUiState.java` (LiveData, fluxo unidirecional) |
| Armazenamento seguro | `data/KeystoreTokenStore.java` (AES-256-GCM com chave no Android Keystore) |
| Testes automatizados | 6 testes unitários (`app/src/test`) e 4 testes instrumentados (`app/src/androidTest`) |
| Preparação para distribuição | `isMinifyEnabled = true` no release e `.gitignore` a excluir `*.jks` e `keystore.properties` |
| Protocolo experimental | `docs/Protocolo_Experimental_Grupo21.pdf` |
| Excertos Java vs. Kotlin | `docs/Excertos_Java_Kotlin.md` |

Conta de demonstração: **aluno / pdm2026**

## Arquitetura

```
MainActivity (View)  ──eventos──▶  LoginViewModel  ──▶  AuthRepository (FakeAuthRepository)
        ▲                               │          ──▶  TokenStore (KeystoreTokenStore)
        └──────── LoginUiState ─────────┘
```

## Como abrir no Android Studio

**Opção A (a mais segura):**
1. *New Project → Empty Views Activity*, linguagem **Java**, package `pt.ubi.pdm.grupo21`, Minimum SDK **API 29**.
2. Substituir a pasta `app/src` do projeto novo pela pasta `app/src` deste zip.
3. Copiar as `dependencies` e o `buildFeatures { viewBinding = true }` de `app/build.gradle.kts` para o `build.gradle.kts` do módulo app (se o Android Studio usar `libs.versions.toml`, podem colar as linhas tal como estão).
4. Sync Gradle e correr.

**Opção B:** *File → Open* sobre esta pasta. O Android Studio pode propor atualizar o Gradle ou o AGP; aceitem.

## Como correr os testes

- **Unitários** (sem emulador): botão direito em `app/src/test/java` → *Run 'Tests in ...'*
- **Interface (Espresso) e Keystore** (com emulador ligado): botão direito em `app/src/androidTest/java` → *Run 'Tests in ...'*
- **Pelo terminal** (na pasta do projeto): `./gradlew test` e `./gradlew connectedAndroidTest` (no Windows: `gradlew.bat test`)

Antes dos testes do Espresso, desliguem as animações do emulador (*Opções de programador* → escalas de animação a 0x) para evitar testes instáveis.

## Guião da demonstração (cerca de 3 minutos)

1. Entrar com `aluno` / `errada` e mostrar a mensagem de erro. Ver o Logcat filtrado por `Grupo21Demo` (`Novo estado: ERROR`).
2. Entrar com `aluno` / `pdm2026` e mostrar o ecrã "Olá, aluno!".
3. **Rodar o ecrã**: o estado mantém-se, porque o ViewModel sobrevive à recriação da Activity.
4. **Fechar a app** (remover dos recentes) e voltar a abrir: entra diretamente, porque o token foi lido e decifrado do Keystore.
5. Opcional: *Device Explorer* → `/data/data/pt.ubi.pdm.grupo21/shared_prefs/secure_session.xml` mostra que o token está cifrado.
6. Terminar sessão.
7. Correr os testes unitários e os do Espresso ao vivo (tudo verde).

# 🍺 Beergod API

API backend desenvolvida para a plataforma **Beergod**, utilizando **Java 25** com os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture** (Arquitetura Limpa). O objetivo do projeto é construir um sistema escalável, testável e totalmente desacoplado de frameworks e infraestrutura externa.

---

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 25
* **Gerenciador de Dependências:** Apache Maven
* **Testes Automatizados:** JUnit 5
* **Arquitetura:** Clean Architecture & Domain-Driven Design (DDD)

---

## 🏛️ Arquitetura do Projeto

A aplicação é dividida em camadas estritas para garantir o desacoplamento das regras de negócio:

```text
src/
├── main/java/org/example/
│   ├── domain/               # Entidades, Exceções de Negócio e Interfaces de Repositório
│   ├── application/          # Casos de Uso (Use Cases / Regras de Negócio)
│   └── infrastructure/       # Implementações técnicas (Persistência, Banco de Dados)
└── test/java/org/example/    # Suíte de Testes Unitários com JUnit 5

Destaques do Código
Validação Fail-Fast: Entidades ricas com validação imediata no construtor para impedir estados inválidos na aplicação.

Isolamento de Regras: Camada de aplicação totalmente independente do banco de dados ou frameworks.

Tratamento Explicito de Exceções: Uso de RegraNegocioException para erros de domínio.

🧪 Testes Automatizados
O projeto conta com uma suíte de testes unitários cobrindo todos os Casos de Uso do catálogo de produtos, garantindo validações de cenários felizes e tratamento de exceções.

Para rodar todos os testes automatizados via terminal:
mvn test

📌 Como Executar
Certifique-se de ter o JDK 25 e o Maven instalados na sua máquina.

Clone o repositório:
git clone [https://github.com/SEU-USUARIO/beergod-api.git](https://github.com/SEU-USUARIO/beergod-api.git)

Acesse o diretório do projeto:
cd beergod-api

Execute a compilação e suíte de testes:
mvn clean test

Próximos Passos (Roadmap)
[x] CRUD completo do catálogo de produtos (Domínio e Aplicação)

[x] Suíte de testes unitários com JUnit 5

[ ] Integração com banco de dados PostgreSQL (Camada de Infraestrutura)

[ ] Implementação do contexto delimitado de Usuários e Autenticação (Perfil Administrador)
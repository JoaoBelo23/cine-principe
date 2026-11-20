# Cine-Príncipe

Sistema desktop de gerenciamento de compras de ingressos para um cinema fictício, desenvolvido em Java como projeto acadêmico das disciplinas de Programação Orientada a Objetos e Análise e Projeto de Software.

O projeto simula o fluxo de compra de ingressos, desde a escolha do filme e da sessão até a seleção de assentos, aplicação de descontos e finalização do pedido.

## Funcionalidades

- Cadastro e gerenciamento de usuários.
- Cadastro e consulta de filmes.
- Seleção de salas, sessões e assentos disponíveis.
- Compra de ingressos e produtos.
- Aplicação de cupons promocionais.
- Regras de precificação para usuários padrão, estudantes e críticos.
- Geração de recibos de compra.
- Persistência de dados em banco SQLite.

## Tecnologias utilizadas

- **Java:** linguagem de programação.
- **Java Swing:** construção da interface gráfica.
- **SQLite:** armazenamento local dos dados.
- **Maven:** gerenciamento de dependências e construção do projeto.
- **JUnit 5:** implementação de testes unitários.
- **JaCoCo:** análise da cobertura dos testes.

## Arquitetura e organização

O projeto possui uma estrutura inspirada no padrão arquitetural MVC (Model-View-Controller), complementada por uma organização em camadas.

Principais diretórios:

| Diretório | Responsabilidade |
|---|---|
| `src/gui/` | Interfaces gráficas e interação com o usuário. |
| `src/control/` | Controladores responsáveis pela comunicação entre interface e lógica do sistema. |
| `src/model/` | Entidades e dados relacionados ao domínio. |
| `src/service/` | Regras de negócio e operações do sistema. |
| `src/data/` | Acesso e persistência dos dados no SQLite. |
| `src/repository/` | Definições auxiliares, como gerenciamento de filmes e tipos de sala. |
| `test/` | Testes automatizados do projeto. |

## Padrões de projeto e refatorações

Durante o desenvolvimento, foram aplicados padrões de projeto e princípios de engenharia de software para melhorar a organização e a manutenção do código.

**Padrões GoF:**
- Strategy: separação das regras de precificação.
- Factory: centralização da criação dos perfis de usuário.
- Facade: simplificação da comunicação com as operações de checkout.
- Adapter: adaptação do acesso aos dados de críticos por meio de uma interface.

Também foram trabalhados princípios SOLID e conceitos GRASP, além de técnicas de refatoração como Extract Class, Hide Delegate, Move Method, Guard Clauses e substituição de condicionais por polimorfismo.

## Testes e qualidade

O projeto utiliza JUnit 5 para testes unitários e JaCoCo para analisar a cobertura dos testes. Também foi desenvolvido um ciclo de TDD na classe `Sessao`, relacionado à tradução e validação da identificação dos assentos.

## Contexto acadêmico

Este repositório reúne a implementação e as refatorações realizadas no sistema Cine-Príncipe como parte das atividades acadêmicas da Universidade Católica de Pernambuco (UNICAP), no curso de Ciência da Computação.

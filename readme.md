## Zotion - Um novo passo para a organização pessoal

O **Zotion** é uma plataforma de organização pessoal desenvolvida como solução para o **Desafio II do Zetta Lab 2025**. O sistema combina gerenciamento de tarefas (lembretes), notas e processamento inteligente de documentos via IA.

---

## Screenshots do Sistema

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/8cceaa47-b294-472f-85e2-907742fff28a" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/dbc38634-80aa-4ba9-978f-3a938731d7f0" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/3f25de21-fa2e-47d5-bfd7-e753823dd032" />

---

## Design

O design completo do sistema, junto a identidade visual e cores usadas estão disponiveis no Figma:
[Acesse Aqui](https://www.figma.com/design/Ym4vqDC5UsUXH5z54DoZlb/Zotion-Project?node-id=0-1&t=K0oLisNZ6mWEiGG8-1).

---

## Modelo de Entidade e Relacionamento

O banco de dados foi projetado para garantir a persistência segura e a integridade dos dados de cada usuário.

![WhatsApp Image 2026-02-04 at 19 39 35](https://github.com/user-attachments/assets/71e9e4d7-6726-47b6-b533-a2a90c1b2380)

---

## 🧪 Testes da API

Caso queira, fiz também um vídeo executando os testes em todas as rotas do sistema: [Acesse Aqui](https://www.youtube.com/watch?v=SkpVbwIDb_Q)


Para facilitar os testes, disponibilizei uma coleção completa do Postman. 

1. Baixe o arquivo: [Zotion_API_Collection.json](./docs/Zotion_API_Collection.json)
2. No Postman, clique em **Import** e selecione o arquivo baixado.
3. Certifique-se de que o ambiente está apontando para `http://localhost:8080`.
   
---

### Explicação das Relações:

* **Usuário (1:N) Lembretes/Notas:** Cada usuário possui sua própria lista privada de lembretes e notas.
* **Usuário (N:M) Perfis:** Um relacionamento **Muitos-para-Muitos** que define o nível de acesso (ex: USER, ADMIN).
* **Automação JPA:** Note que não criamos manualmente a tabela de junção `usuarios_perfis`. Graças à anotação `@JoinTable` no código Java, o **Hibernate/JPA cria automaticamente** essa tabela intermediária para gerenciar as chaves estrangeiras de usuários e perfis.


---

## Como Executar o Projeto com Docker

Este projeto utiliza **Docker** para garantir que o ambiente de execução seja idêntico em qualquer máquina.

### Pré-requisitos:

* Docker e Docker Compose instalados.
* Uma chave de API do Google Gemini.

### Passo a Passo:

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/zotion.git
cd zotion

```


2. **Configure as variáveis de ambiente:**
* Crie um arquivo `.env` na raiz do projeto baseado no `.env.example`.


3. **Suba os containers:**
```bash
docker-compose up --build -d

```


4. **Acesse o sistema:**
* Frontend: `http://localhost` (Gerenciado pelo Nginx).
IMPORTANTE: ao acessar, use http e não https!!

* Backend (API): `http://localhost:8080/api`.



---

## Configuração do Google Gemini API

O Zotion utiliza o modelo **Gemini 2.5 Flash Lite** para extrair lembretes automaticamente de PDFs e imagens.

1. Acesse o [Google AI Studio](https://aistudio.google.com/).
2. Clique em **"Get API key"**.
3. Crie uma nova chave em um projeto novo ou existente.
4. Copie a chave e cole no seu arquivo `.env` na variável `GEMINI_API_KEY`.

---

## Como foi feita a implementação com a LLM?

Para respondermos essa pergunta, existe um conceito muito importante: O TOKEN!
Para essas requisições, temos um gasto de processamento com a LLM e caso passemos do limite, ele encerraria sem retorno.
Por isso, como a ideia é você poder testar a função - mesmo sem ter o plano pago do Gemini - Fizemos algumas "manobras" para que ele funcione:

- Se o arquivo PDF for naturalmente digital, a biblioteca APACHE PDF BOX do backend extrai o texto e envia somente o texto para o Gemini, o que é mais barato que o modal de arquivo.
- Caso o PDF não possua textos "Digitais", ele será enviado realmente como arquivo pois precisamos do OCR fornecido pela IA..

---

##  Variáveis de Ambiente (`.env.example`)

Coloque a chave que você conseguiu seguindo os passos anterires e certifique-se que esse arquivo estará na raiz do projeto. 
(Caso queira, existe o arquivo .env.example já no local correto. Basta renomea-lo de .env.example para .env)
Copie o conteúdo abaixo e salve como `.env` para configurar seu ambiente local:

```
GEMINI_API_KEY=sua_chave_aqui

```

---

##  Requisitos Técnicos Atendidos (Desafio I)

- **Autenticação:** Implementada via Token JWT. 

- **CRUD Completo:** Tarefas (Lembretes) e Notas com filtragem por status.

- **Arquitetura:** Organizada em camadas (Controllers, Services, Models, Repositories).

- **Containerização:** Docker e Docker Compose configurados para toda a aplicação.

- **Frontend:** Desenvolvido em React com Vite e TypeScript para alta performance.

- **SWAGGER-UI:** Foi implementada a biblioteca SpringDoc, que permite a quem for usar a api ter mapeado os endpoints para requisição.
Para acessar o Swagger, acesse http://localhost/swagger-ui/index.html

---

**Desenvolvido por:** Giovane Felipe Godoi Oliveira

# 🍺 BeerGod - Cardápio Digital & Painel Administrativo

Front-end moderno, responsivo e de alta performance desenvolvido para a cervejaria e pub artesanal **BeerGod**. 
Criado com **HTML5**, **Tailwind CSS (CDN)** e **JavaScript modular puro (ES Modules)**, sem necessidade de etapas de compilação ou bundlers (sem Vite/Webpack), 100% pronto para execução local e deploy no **GitHub Pages**.

---

## 🎨 Identidade Visual & Design System

Inspirado na atmosfera rústica e sofisticada de pubs cervejeiros britânicos e tap houses artesanais:
- **Background Principal:** `#0f172a` (Slate 900) e `#111827` (Gray 900) com gradiente radial sutil e textura de malte/cervejaria.
- **Cards e Modais:** `#1e293b` (Slate 800) com bordas em `#334155` (Slate 700) e efeitos de vidro (*glassmorphism*).
- **Destaques e Acentos:** Dourado/Âmbar `#f59e0b` (Amber 500) com efeitos de brilho suave (*amber glow*).
- **Tipografia:** Tipografia principal com legibilidade cristalina (*Plus Jakarta Sans*) e títulos de destaque artesanal (*Playfair Display*).
- **Badges Especiais:**
  - Tag de categoria do produto (`categoriaNome`).
  - Badges extras para bebidas (`tipo === "BEBIDA"`): marca/fabricante (`marca`) e volume em ml (`${tamanhoML}ml`).
  - Badges de status no painel admin: Ativo (verde esmeralda pulsante) e Inativo (rosé suave).
- **Sistema de Notificações (Toast):**
  - Fixado no canto superior direito (`fixed top-4 right-4 z-50`).
  - Barra de progresso animada e fechamento manual ou automático (~4s).
  - Suporte a `success`, `error`, `warning` e `info`.
  - Captura imediata do campo `mensagem` em respostas HTTP 400 da API (Regra de Negócio violada).

---

## 📂 Estrutura de Arquivos

```text
frontend/
├── index.html            # Cardápio Digital Público (Mobile-First para QR Code na mesa)
├── admin.html            # Login e Painel de Gestão Administrativa (3 Abas)
├── beergod.png           # Logotipo oficial da cervejaria
├── style.css             # Estilos customizados, scrollbars elegantes e animações
├── serve.ps1             # Script PowerShell para iniciar servidor web local com 1 clique
├── README.md             # Documentação técnica e guia de execução
└── js/
    ├── config.js         # Exportação da URL base da API (API_BASE_URL)
    ├── api.js            # Cliente HTTP modular com tratamento de erros (400 mensagem)
    ├── toast.js          # Sistema de notificações flutuantes toast
    ├── utils.js          # Formatadores BRL, gerador de placeholders SVG e session storage
    ├── menu.js           # Lógica do Cardápio Público (filtros, busca em tempo real, badges)
    └── admin.js          # Lógica do Painel Admin (login, abas, CRUD de produtos, categorias e admins)
```

---

## ⚙️ Configuração da API

A URL base do backend está configurada em `js/config.js`:

```javascript
export const API_BASE_URL = "http://localhost:8080";
```

Todas as requisições utilizam essa constante. Quando for realizar deploy em produção, basta atualizar essa URL para o domínio do seu servidor Spring Boot.

---

## 🚀 Telas & Funcionalidades

### 1. Cardápio Público (`index.html`)
- **Cabeçalho:** Logo BeerGod, título estilizado, busca por texto em tempo real e botão discreto "Área Admin".
- **Filtro Horizontal de Categorias:** Carregado dinamicamente via `GET /categorias?apenasAtivas=true`, com o botão "Todos" ativo por padrão e rolagem horizontal suave no celular.
- **Catálogo de Produtos:** Grid responsivo carregado via `GET /produtos?apenasAtivos=true`.
- **Card de Produto:**
  - Imagem com fallback automático para placeholder SVG elegante caso a URL seja inválida ou falhe o carregamento.
  - Tag da categoria.
  - Nome e descrição detalhada.
  - Preço formatado em BRL (`R$ 00,00`).
  - Badges especiais para bebidas (`tipo === "BEBIDA"`): marca e volume `${tamanhoML}ml`.
  - Modal de detalhes rápidos ao clicar em "Detalhes".

### 2. Login e Painel Administrativo (`admin.html`)
- **Autenticação Segura:**
  - Caso não haja administrador logado (`beergod_admin` no `localStorage`), exibe exclusivamente o card de Login centralizado.
  - Envio de credenciais para `POST /administradores/login`.
  - Em caso de sucesso (200 OK), salva `{ id, nome, email, ativo }` no `localStorage` e libera o painel administrativo.
  - Em caso de erro (400), lê `data.mensagem` e exibe no Toast.
- **Cabeçalho Administrativo:** Exibe nome e e-mail do admin conectado, link "Ver Cardápio" (abre o cardápio público em nova aba) e botão "Sair" (limpa a sessão).
- **Navegação por 3 Abas:**
  - **Aba 1: Produtos & Bebidas**
    - Listagem completa via `GET /produtos` com indicador de status (Ativo/Inativo), filtros rápidos por texto, categoria e status.
    - **Seleção & Ações em Lote (Batch Actions):** Checkboxes em cada linha e checkbox mestre no cabeçalho. Barra de ações flutuante com contador em tempo real exibindo ações em lote executadas em paralelo via `Promise.all`:
      - **Alterar Preço:** modal para definir novo valor em R$ para todos os itens selecionados.
      - **Ativar:** ativação em lote com confirmação.
      - **Inativar:** inativação em lote com confirmação.
    - Modal de Criação (`POST /produtos`) e Edição (`PUT /produtos/{id}`).
    - Switch dinâmico **"É uma Bebida?"**: quando ativado, exibe e exige os campos `tamanhoML` e `marca`. Quando desmarcado, envia `tamanhoML: null` e `marca: null`.
    - Pré-visualização ao vivo da URL da imagem.
    - Botão de Inativar (`DELETE /produtos/{id}`) e Reativar (`PUT /produtos/{id}` com `ativo: true`).
  - **Aba 2: Categorias**
    - Listagem completa via `GET /categorias` com contagem de produtos associados.
    - Formulário rápido para criar nova categoria (`POST /categorias`).
    - Modal para editar nome e status (`PUT /categorias/{id}`).
    - Botão de Inativar (`DELETE /categorias/{id}`) e Reativar (`PUT /categorias/{id}` com `ativo: true`).
  - **Aba 3: Administradores**
    - Listagem via `GET /administradores`.
    - Destaque especial "(Você)" para o administrador logado.
    - Criação (`POST /administradores`) com validação de senha mínima de 6 caracteres.
    - Edição (`PUT /administradores/{id}`) com aviso de que deixar a senha em branco mantém a senha atual intacta.
    - Inativação com confirmação de segurança (`DELETE /administradores/{id}`) e reativação (`PUT /administradores/{id}`).

---

## 💻 Como Executar Localmente

Por utilizar **ES Modules** (`import` / `export`), os arquivos devem ser servidos via protocolo HTTP/HTTPS (e não diretamente como `file:///`).

### Opção 1: Script PowerShell Integrado (Mais rápido no Windows)
Abra o terminal na pasta `frontend/` e execute:
```powershell
.\serve.ps1
```
O script iniciará um servidor web local na porta `3000` e abrirá automaticamente seu navegador!

### Opção 2: VS Code Live Server
1. Abra a pasta `frontend` no VS Code.
2. Clique com o botão direito em `index.html` e selecione **"Open with Live Server"**.

### Opção 3: Node.js / NPM (se instalado)
```bash
npx serve .
# ou
npx http-server -p 3000
```

### Opção 4: Python (se instalado)
```bash
python -m http.server 3000
```

---

## 🌐 Deploy no GitHub Pages

O projeto já utiliza caminhos relativos em todos os scripts, links e imagens (`./style.css`, `./js/...`, `./beergod.png`), estando 100% preparado para o GitHub Pages:

1. No seu repositório no GitHub, acesse **Settings** > **Pages**.
2. Em **Build and deployment** > **Source**, selecione **Deploy from a branch**.
3. Selecione a branch principal (`main`) e a pasta onde o frontend se encontra (se estiver na raiz do repositório ou pasta `/frontend` ou `/docs`).
4. Clique em **Save**. O site estará no ar em poucos segundos com HTTPS automático!

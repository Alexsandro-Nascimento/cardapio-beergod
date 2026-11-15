import { API_BASE_URL } from "./config.js";
import { getStoredAdmin, removeStoredAdmin } from "./utils.js";

async function apiRequest(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;

  const defaultHeaders = {};
  if (options.body && typeof options.body === "string") {
    defaultHeaders["Content-Type"] = "application/json";
  }

  const storedAdmin = getStoredAdmin();
  if (storedAdmin && storedAdmin.token) {
    defaultHeaders["Authorization"] = `Bearer ${storedAdmin.token}`;
  }

  const fetchOptions = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
  };

  let response;
  try {
    response = await fetch(url, fetchOptions);
  } catch (err) {
    const error = new Error(
        `Falha de conexão com a API (${API_BASE_URL}). Verifique se o servidor backend está em execução.`
    );
    error.isNetworkError = true;
    throw error;
  }

  if (response.status === 204) {
    return null;
  }

  let data = null;
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    try {
      data = await response.json();
    } catch {
      data = null;
    }
  } else {
    try {
      const text = await response.text();
      data = text ? { mensagem: text } : null;
    } catch {
      data = null;
    }
  }

  if (!response.ok) {
    // Se o token expirou ou for inválido fora da tela de login, encerra a sessão
    if (response.status === 401 && endpoint !== "/administradores/login") {
      removeStoredAdmin();
      window.dispatchEvent(new Event("beergod:unauthorized"));
    }

    const message =
        (data && data.mensagem) ||
        (data && data.erro) ||
        `Erro ${response.status}: ${response.statusText || "Falha na requisição"}`;

    const error = new Error(message);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

/* ==========================================================================
   Categorias API
   ========================================================================== */

export async function getCategorias(apenasAtivas = false) {
  const query = apenasAtivas ? "?apenasAtivas=true" : "";
  return await apiRequest(`/categorias${query}`, { method: "GET" });
}

export async function criarCategoria(categoriaData) {
  return await apiRequest("/categorias", {
    method: "POST",
    body: JSON.stringify({
      nome: categoriaData.nome?.trim(),
      ativo: categoriaData.ativo !== undefined ? categoriaData.ativo : true,
    }),
  });
}

export async function atualizarCategoria(id, categoriaData) {
  return await apiRequest(`/categorias/${id}`, {
    method: "PUT",
    body: JSON.stringify({
      nome: categoriaData.nome?.trim(),
      ativo: categoriaData.ativo,
    }),
  });
}

export async function inativarCategoria(id) {
  return await apiRequest(`/categorias/${id}`, { method: "DELETE" });
}

/* ==========================================================================
   Produtos & Bebidas API
   ========================================================================== */

export async function getProdutos(apenasAtivos = false) {
  const query = apenasAtivos ? "?apenasAtivos=true" : "";
  return await apiRequest(`/produtos${query}`, { method: "GET" });
}

export async function criarProduto(produtoData) {
  const isBebida = Boolean(produtoData.isBebida);
  const payload = {
    nome: produtoData.nome?.trim(),
    descricao: produtoData.descricao?.trim() || "",
    preco: parseFloat(produtoData.preco),
    imagemUrl: produtoData.imagemUrl?.trim() || "",
    ativo: produtoData.ativo !== undefined ? produtoData.ativo : true,
    categoriaId: Number(produtoData.categoriaId),
    tamanhoML: isBebida && produtoData.tamanhoML ? parseInt(produtoData.tamanhoML, 10) : null,
    marca: isBebida && produtoData.marca?.trim() ? produtoData.marca.trim() : null,
  };

  return await apiRequest("/produtos", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function atualizarProduto(id, produtoData) {
  const isBebida = Boolean(produtoData.isBebida);
  const payload = {
    nome: produtoData.nome?.trim(),
    descricao: produtoData.descricao?.trim() || "",
    preco: parseFloat(produtoData.preco),
    imagemUrl: produtoData.imagemUrl?.trim() || "",
    ativo: produtoData.ativo !== undefined ? produtoData.ativo : true,
    categoriaId: Number(produtoData.categoriaId),
    tamanhoML: isBebida && produtoData.tamanhoML ? parseInt(produtoData.tamanhoML, 10) : null,
    marca: isBebida && produtoData.marca?.trim() ? produtoData.marca.trim() : null,
  };

  return await apiRequest(`/produtos/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export async function inativarProduto(id) {
  return await apiRequest(`/produtos/${id}`, { method: "DELETE" });
}

/* ==========================================================================
   Administradores API
   ========================================================================== */

export async function loginAdministrador(credentials) {
  return await apiRequest("/administradores/login", {
    method: "POST",
    body: JSON.stringify({
      email: credentials.email?.trim(),
      senha: credentials.senha,
    }),
  });
}

export async function getAdministradores() {
  return await apiRequest("/administradores", { method: "GET" });
}

export async function criarAdministrador(adminData) {
  return await apiRequest("/administradores", {
    method: "POST",
    body: JSON.stringify({
      nome: adminData.nome?.trim(),
      email: adminData.email?.trim(),
      senha: adminData.senha,
    }),
  });
}

export async function atualizarAdministrador(id, adminData) {
  // If password is blank or empty, send null so backend retains current password
  const senha = adminData.senha && adminData.senha.trim().length > 0 ? adminData.senha : null;

  return await apiRequest(`/administradores/${id}`, {
    method: "PUT",
    body: JSON.stringify({
      nome: adminData.nome?.trim(),
      email: adminData.email?.trim(),
      senha: senha,
      ativo: adminData.ativo,
    }),
  });
}

export async function inativarAdministrador(id) {
  return await apiRequest(`/administradores/${id}`, { method: "DELETE" });
}

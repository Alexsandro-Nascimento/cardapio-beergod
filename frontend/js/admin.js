import {
  loginAdministrador,
  getAdministradores,
  criarAdministrador,
  atualizarAdministrador,
  inativarAdministrador,
  getCategorias,
  criarCategoria,
  atualizarCategoria,
  inativarCategoria,
  getProdutos,
  criarProduto,
  atualizarProduto,
  inativarProduto,
} from "./api.js";

import {
  getStoredAdmin,
  setStoredAdmin,
  removeStoredAdmin,
  isAuthenticated,
  formatBRL,
  escapeHtml,
  getPlaceholderImage,
} from "./utils.js";

import { toast } from "./toast.js";

/* ==========================================================================
   State Management
   ========================================================================== */
let currentAdmin = null;
let activeTab = "produtos"; // "produtos" | "categorias" | "administradores"

let produtosList = [];
let categoriasList = [];
let administradoresList = [];

// Batch Selection State
let selectedProdutoIds = new Set();

// Edit / Modal State
let editingProdutoId = null;
let editingCategoriaId = null;
let editingAdminId = null;

// Search / Filter states
let produtoSearch = "";
let produtoCategoryFilter = "all";
let produtoStatusFilter = "all";

let categoriaSearch = "";
let adminSearch = "";

/* ==========================================================================
   Initialization
   ========================================================================== */
export function initAdmin() {
  currentAdmin = getStoredAdmin();
  setupGlobalEvents();

  if (isAuthenticated()) {
    showDashboardView();
  } else {
    showLoginView();
  }
}

function setupGlobalEvents() {
  // Login Form
  const loginForm = document.getElementById("login-form");
  if (loginForm) {
    loginForm.addEventListener("submit", handleLoginSubmit);
  }

  // Logout Button
  const logoutBtn = document.getElementById("logout-btn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", handleLogout);
  }

  // Tab Switching Buttons
  const tabButtons = document.querySelectorAll("[data-tab-target]");
  tabButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const target = btn.getAttribute("data-tab-target");
      switchTab(target);
    });
  });

  // Setup Product Modal & Forms
  setupProductFormEvents();

  // Setup Category Modal & Forms
  setupCategoryFormEvents();

  // Setup Administrator Modal & Forms
  setupAdminFormEvents();

  // Image Fallback handler
  window.__beergodAdminImageFallback = function (img, isBebida) {
    img.onerror = null;
    img.src = getPlaceholderImage("BeerGod", isBebida);
  };
}

/* ==========================================================================
   Authentication Views (Login vs Dashboard)
   ========================================================================== */
function showLoginView() {
  const loginView = document.getElementById("login-view");
  const dashboardView = document.getElementById("dashboard-view");

  if (loginView) loginView.classList.remove("hidden");
  if (dashboardView) dashboardView.classList.add("hidden");

  // Reset form
  const loginForm = document.getElementById("login-form");
  if (loginForm) loginForm.reset();
}

function showDashboardView() {
  currentAdmin = getStoredAdmin();

  const loginView = document.getElementById("login-view");
  const dashboardView = document.getElementById("dashboard-view");

  if (loginView) loginView.classList.add("hidden");
  if (dashboardView) dashboardView.classList.remove("hidden");

  // Display admin info in header
  const adminNameEl = document.getElementById("header-admin-name");
  const adminEmailEl = document.getElementById("header-admin-email");
  if (adminNameEl && currentAdmin) adminNameEl.textContent = currentAdmin.nome;
  if (adminEmailEl && currentAdmin) adminEmailEl.textContent = currentAdmin.email;

  // Load initial tab data
  switchTab(activeTab);
}

async function handleLoginSubmit(e) {
  e.preventDefault();
  const form = e.target;
  const submitBtn = form.querySelector("button[type='submit']");
  const originalBtnText = submitBtn.innerHTML;

  const email = form.email.value.trim();
  const senha = form.senha.value;

  if (!email || !senha) {
    toast.warning("Preencha todos os campos para fazer login.");
    return;
  }

  try {
    submitBtn.disabled = true;
    submitBtn.innerHTML = `
      <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-slate-950 inline" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      Entrando...
    `;

    const adminData = await loginAdministrador({ email, senha });

    if (!adminData.ativo) {
      toast.error("Esta conta de administrador foi inativada.");
      return;
    }

    // Save { id, nome, email, ativo } to localStorage
    setStoredAdmin({
      id: adminData.id,
      nome: adminData.nome,
      email: adminData.email,
      ativo: adminData.ativo,
    });

    toast.success(`Bem-vindo, ${adminData.nome}!`);
    showDashboardView();
  } catch (err) {
    console.error("Erro no login:", err);
    toast.error(err.message || "E-mail ou senha inválidos.");
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = originalBtnText;
  }
}

function handleLogout() {
  removeStoredAdmin();
  currentAdmin = null;
  toast.info("Sessão finalizada com sucesso.");
  showLoginView();
}

/* ==========================================================================
   Tab Navigation
   ========================================================================== */
function switchTab(tabName) {
  activeTab = tabName;

  // Update tab buttons style
  const tabButtons = document.querySelectorAll("[data-tab-target]");
  tabButtons.forEach((btn) => {
    const isTarget = btn.getAttribute("data-tab-target") === tabName;
    if (isTarget) {
      btn.className =
        "flex items-center gap-2 py-3 px-4 border-b-2 border-amber-500 text-amber-400 font-bold text-sm transition-all";
    } else {
      btn.className =
        "flex items-center gap-2 py-3 px-4 border-b-2 border-transparent text-slate-400 hover:text-slate-200 font-medium text-sm transition-all";
    }
  });

  // Hide/Show tab contents
  const tabs = ["produtos", "categorias", "administradores"];
  tabs.forEach((tab) => {
    const el = document.getElementById(`tab-content-${tab}`);
    if (el) {
      if (tab === tabName) {
        el.classList.remove("hidden");
      } else {
        el.classList.add("hidden");
      }
    }
  });

  // Trigger data load
  if (tabName === "produtos") {
    loadProdutosData();
  } else {
    selectedProdutoIds.clear();
    updateBatchActionsBar();
    if (tabName === "categorias") {
      loadCategoriasData();
    } else if (tabName === "administradores") {
      loadAdministradoresData();
    }
  }
}

/* ==========================================================================
   ABA 1: PRODUTOS & BEBIDAS
   ========================================================================== */
async function loadProdutosData() {
  const tableBody = document.getElementById("produtos-table-body");
  if (tableBody) {
    tableBody.innerHTML = `<tr><td colspan="8" class="text-center py-8 text-slate-400">Carregando catálogo de produtos...</td></tr>`;
  }

  try {
    // Load both products and categories (for select options and category names)
    const [prods, cats] = await Promise.all([getProdutos(false), getCategorias(false)]);

    produtosList = Array.isArray(prods) ? prods : [];
    categoriasList = Array.isArray(cats) ? cats : [];

    selectedProdutoIds.clear();
    populateCategorySelectOptions();
    renderProdutosTable();
    updateProdutosCountBadge();
  } catch (err) {
    console.error("Erro ao carregar produtos:", err);
    toast.error(err.message || "Erro ao carregar lista de produtos.");
    if (tableBody) {
      tableBody.innerHTML = `<tr><td colspan="8" class="text-center py-8 text-rose-400 font-semibold">${escapeHtml(
        err.message
      )}</td></tr>`;
    }
  }
}

function updateProdutosCountBadge() {
  const badge = document.getElementById("badge-count-produtos");
  if (badge) badge.textContent = produtosList.length;
}

function populateCategorySelectOptions() {
  const select = document.getElementById("produto-categoriaId");
  const filterSelect = document.getElementById("produto-category-filter");

  if (select) {
    select.innerHTML = `<option value="">Selecione uma categoria...</option>` +
      categoriasList
        .filter((c) => c.ativo)
        .map((c) => `<option value="${c.id}">${escapeHtml(c.nome)}</option>`)
        .join("");
  }

  if (filterSelect) {
    filterSelect.innerHTML = `<option value="all">Todas as Categorias</option>` +
      categoriasList
        .map((c) => `<option value="${c.id}">${escapeHtml(c.nome)}</option>`)
        .join("");
  }
}

function getFilteredProdutos() {
  return produtosList.filter((prod) => {
    // Search by name, brand, or category
    const matchSearch =
      produtoSearch === "" ||
      prod.nome.toLowerCase().includes(produtoSearch) ||
      (prod.descricao && prod.descricao.toLowerCase().includes(produtoSearch)) ||
      (prod.marca && prod.marca.toLowerCase().includes(produtoSearch)) ||
      (prod.categoriaNome && prod.categoriaNome.toLowerCase().includes(produtoSearch));

    // Category filter
    const matchCat =
      produtoCategoryFilter === "all" || String(prod.categoriaId) === String(produtoCategoryFilter);

    // Status filter
    const matchStatus =
      produtoStatusFilter === "all" ||
      (produtoStatusFilter === "ativos" && prod.ativo) ||
      (produtoStatusFilter === "inativos" && !prod.ativo);

    return matchSearch && matchCat && matchStatus;
  });
}

function updateBatchActionsBar(filtered = null) {
  const bar = document.getElementById("produtos-batch-bar");
  const countEl = document.getElementById("batch-selected-count");
  const selectAll = document.getElementById("select-all-produtos");

  const totalSelected = selectedProdutoIds.size;
  if (countEl) countEl.textContent = totalSelected;

  if (bar) {
    if (totalSelected > 0) {
      bar.classList.remove("hidden");
    } else {
      bar.classList.add("hidden");
    }
  }

  if (selectAll) {
    if (!filtered) filtered = getFilteredProdutos();
    if (filtered.length === 0) {
      selectAll.checked = false;
      selectAll.indeterminate = false;
    } else {
      const selectedFilteredCount = filtered.filter((p) => selectedProdutoIds.has(p.id)).length;
      if (selectedFilteredCount === 0) {
        selectAll.checked = false;
        selectAll.indeterminate = false;
      } else if (selectedFilteredCount === filtered.length) {
        selectAll.checked = true;
        selectAll.indeterminate = false;
      } else {
        selectAll.checked = false;
        selectAll.indeterminate = true;
      }
    }
  }
}

async function executeBatchUpdate(changes, successActionText) {
  const ids = Array.from(selectedProdutoIds);
  if (ids.length === 0) return;

  try {
    const promises = ids.map((id) => {
      const prod = produtosList.find((p) => p.id === id);
      if (!prod) return Promise.resolve();

      const isBebida = prod.tipo === "BEBIDA";
      const payload = {
        nome: prod.nome,
        descricao: prod.descricao,
        preco: changes.preco !== undefined ? changes.preco : prod.preco,
        imagemUrl: prod.imagemUrl,
        categoriaId: prod.categoriaId,
        isBebida: isBebida,
        tamanhoML: isBebida ? prod.tamanhoML : null,
        marca: isBebida ? prod.marca : null,
        ativo: changes.ativo !== undefined ? changes.ativo : prod.ativo,
      };

      return atualizarProduto(id, payload);
    });

    await Promise.all(promises);
    toast.success(`${successActionText} ${ids.length} produto(s)!`);
    selectedProdutoIds.clear();
    await loadProdutosData();
  } catch (err) {
    console.error("Erro na atualização em lote:", err);
    toast.error(err.message || "Erro ao processar atualização em lote.");
  }
}

function renderProdutosTable() {
  const tableBody = document.getElementById("produtos-table-body");
  const mobileCardsContainer = document.getElementById("produtos-mobile-cards");
  if (!tableBody) return;

  const filtered = getFilteredProdutos();

  if (filtered.length === 0) {
    tableBody.innerHTML = `
      <tr>
        <td colspan="8" class="text-center py-12 text-slate-400">
          <div class="flex flex-col items-center justify-center">
            <svg class="w-10 h-10 text-slate-500 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p class="font-medium">Nenhum produto cadastrado ou correspondente aos filtros.</p>
          </div>
        </td>
      </tr>
    `;
    if (mobileCardsContainer) {
      mobileCardsContainer.innerHTML = `
        <div class="text-center py-8 text-slate-400 text-sm">Nenhum produto encontrado.</div>
      `;
    }
    updateBatchActionsBar(filtered);
    return;
  }

  // Desktop Table Rows
  tableBody.innerHTML = filtered
    .map((prod) => {
      const isBebida = prod.tipo === "BEBIDA";
      const isSelected = selectedProdutoIds.has(prod.id);
      const imageSrc = prod.imagemUrl && prod.imagemUrl.trim().length > 0
        ? escapeHtml(prod.imagemUrl)
        : getPlaceholderImage(prod.nome, isBebida);

      return `
      <tr class="border-b border-slate-700/60 hover:bg-slate-800/50 transition-colors ${
        isSelected ? "bg-amber-500/10" : ""
      }">
        <!-- Checkbox Selection -->
        <td class="w-10 py-3 px-4 text-center">
          <input
            type="checkbox"
            data-produto-id="${prod.id}"
            class="produto-row-checkbox w-4 h-4 rounded text-amber-500 focus:ring-amber-500 bg-slate-800 border-slate-700 cursor-pointer accent-amber-500"
            ${isSelected ? "checked" : ""}
            title="Selecionar produto"
          />
        </td>

        <!-- Image & Name -->
        <td class="py-3 px-4">
          <div class="flex items-center gap-3">
            <img
              src="${imageSrc}"
              alt="${escapeHtml(prod.nome)}"
              onerror="window.__beergodAdminImageFallback(this, ${isBebida})"
              class="w-12 h-12 rounded-xl object-cover bg-slate-900 border border-slate-700 flex-shrink-0"
            />
            <div class="min-w-0">
              <div class="font-bold text-slate-100 truncate">${escapeHtml(prod.nome)}</div>
              <div class="text-xs text-slate-400 truncate max-w-xs">${escapeHtml(prod.descricao) || "Sem descrição"}</div>
            </div>
          </div>
        </td>

        <!-- Type -->
        <td class="py-3 px-4">
          ${
            isBebida
              ? `<span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-md text-xs font-semibold bg-amber-500/10 text-amber-300 border border-amber-500/30">
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" /></svg>
                  Bebida
                </span>`
              : `<span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-semibold bg-slate-700/60 text-slate-300 border border-slate-600/40">
                  Produto
                </span>`
          }
        </td>

        <!-- Category -->
        <td class="py-3 px-4">
          <span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-slate-800 text-slate-300 border border-slate-700">
            ${escapeHtml(prod.categoriaNome || "Sem categoria")}
          </span>
        </td>

        <!-- Drink details: volume / brand -->
        <td class="py-3 px-4 text-xs">
          ${
            isBebida
              ? `
              <div class="flex flex-col gap-0.5">
                <span class="font-semibold text-slate-200">${escapeHtml(prod.marca || "-")}</span>
                <span class="text-slate-400 font-mono">${prod.tamanhoML ? `${prod.tamanhoML}ml` : "-"}</span>
              </div>
            `
              : `<span class="text-slate-500">-</span>`
          }
        </td>

        <!-- Price -->
        <td class="py-3 px-4">
          <span class="font-extrabold text-amber-400 font-serif-brand text-sm">
            ${formatBRL(prod.preco)}
          </span>
        </td>

        <!-- Status -->
        <td class="py-3 px-4">
          ${
            prod.ativo
              ? `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                  Ativo
                </span>`
              : `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-rose-400"></span>
                  Inativo
                </span>`
          }
        </td>

        <!-- Actions -->
        <td class="py-3 px-4 text-right">
          <div class="flex items-center justify-end gap-2">
            <button
              type="button"
              onclick="window.openEditProdutoModal(${prod.id})"
              class="p-1.5 rounded-lg text-slate-300 hover:text-amber-400 hover:bg-slate-700/60 transition-colors"
              title="Editar Produto"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>

            ${
              prod.ativo
                ? `
              <button
                type="button"
                onclick="window.confirmInativarProduto(${prod.id})"
                class="p-1.5 rounded-lg text-rose-400 hover:text-rose-300 hover:bg-rose-500/10 transition-colors"
                title="Inativar Produto"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>`
                : `
              <button
                type="button"
                onclick="window.confirmReativarProduto(${prod.id})"
                class="p-1.5 rounded-lg text-emerald-400 hover:text-emerald-300 hover:bg-emerald-500/10 transition-colors"
                title="Reativar Produto"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </button>`
            }
          </div>
        </td>
      </tr>
      `;
    })
    .join("");

  // Mobile Cards View
  if (mobileCardsContainer) {
    mobileCardsContainer.innerHTML = filtered
      .map((prod) => {
        const isBebida = prod.tipo === "BEBIDA";
        const isSelected = selectedProdutoIds.has(prod.id);
        const imageSrc = prod.imagemUrl && prod.imagemUrl.trim().length > 0
          ? escapeHtml(prod.imagemUrl)
          : getPlaceholderImage(prod.nome, isBebida);

        return `
        <div class="p-4 rounded-xl bg-slate-800/80 border border-slate-700 flex flex-col gap-3 ${
          isSelected ? "ring-2 ring-amber-500/60 bg-amber-500/5" : ""
        }">
          <div class="flex items-center justify-between pb-2 border-b border-slate-700/60">
            <label class="flex items-center gap-2 cursor-pointer select-none">
              <input
                type="checkbox"
                data-produto-id="${prod.id}"
                class="produto-row-checkbox w-4 h-4 rounded text-amber-500 focus:ring-amber-500 bg-slate-800 border-slate-700 cursor-pointer accent-amber-500"
                ${isSelected ? "checked" : ""}
              />
              <span class="text-xs font-semibold text-slate-300">Selecionar item</span>
            </label>
            <span class="font-extrabold text-amber-400 font-serif-brand text-sm">${formatBRL(prod.preco)}</span>
          </div>

          <div class="flex items-start gap-3">
            <img
              src="${imageSrc}"
              alt="${escapeHtml(prod.nome)}"
              onerror="window.__beergodAdminImageFallback(this, ${isBebida})"
              class="w-16 h-16 rounded-xl object-cover bg-slate-900 border border-slate-700 flex-shrink-0"
            />
            <div class="flex-1 min-w-0">
              <h4 class="font-bold text-slate-100 text-sm truncate">${escapeHtml(prod.nome)}</h4>
              <p class="text-xs text-slate-400 mt-1 line-clamp-1">${escapeHtml(prod.descricao) || "Sem descrição"}</p>
              <div class="flex items-center gap-2 mt-2">
                <span class="text-[10px] px-2 py-0.5 rounded bg-slate-700 text-slate-300">${escapeHtml(prod.categoriaNome || "Geral")}</span>
                ${
                  prod.ativo
                    ? `<span class="text-[10px] px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 font-bold">Ativo</span>`
                    : `<span class="text-[10px] px-2 py-0.5 rounded bg-rose-500/10 text-rose-400 font-bold">Inativo</span>`
                }
              </div>
            </div>
          </div>

          <div class="flex items-center justify-end gap-2 pt-2 border-t border-slate-700/60">
            <button
              type="button"
              onclick="window.openEditProdutoModal(${prod.id})"
              class="px-3 py-1.5 rounded-lg text-xs font-semibold bg-slate-700 hover:bg-slate-600 text-slate-200 transition-colors"
            >
              Editar
            </button>
            ${
              prod.ativo
                ? `<button
                    type="button"
                    onclick="window.confirmInativarProduto(${prod.id})"
                    class="px-3 py-1.5 rounded-lg text-xs font-semibold bg-rose-500/20 text-rose-300 hover:bg-rose-500/30 transition-colors"
                  >Inativar</button>`
                : `<button
                    type="button"
                    onclick="window.confirmReativarProduto(${prod.id})"
                    class="px-3 py-1.5 rounded-lg text-xs font-semibold bg-emerald-500/20 text-emerald-300 hover:bg-emerald-500/30 transition-colors"
                  >Reativar</button>`
            }
          </div>
        </div>
      `;
      })
      .join("");
  }

  // Row checkbox change listeners
  document.querySelectorAll(".produto-row-checkbox").forEach((cb) => {
    cb.addEventListener("change", (e) => {
      const id = Number(e.target.getAttribute("data-produto-id"));
      if (e.target.checked) {
        selectedProdutoIds.add(id);
      } else {
        selectedProdutoIds.delete(id);
      }

      // Sync checkbox state between desktop and mobile if rendered simultaneously
      document.querySelectorAll(`.produto-row-checkbox[data-produto-id="${id}"]`).forEach((el) => {
        el.checked = e.target.checked;
      });

      // Highlight row if desktop
      const tr = e.target.closest("tr");
      if (tr) {
        if (e.target.checked) {
          tr.classList.add("bg-amber-500/10");
        } else {
          tr.classList.remove("bg-amber-500/10");
        }
      }

      updateBatchActionsBar(filtered);
    });
  });

  updateBatchActionsBar(filtered);
}

function setupProductFormEvents() {
  // Search and filter inputs
  const searchInput = document.getElementById("produto-search-input");
  const catFilter = document.getElementById("produto-category-filter");
  const statusFilter = document.getElementById("produto-status-filter");

  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      produtoSearch = e.target.value.trim().toLowerCase();
      renderProdutosTable();
    });
  }

  if (catFilter) {
    catFilter.addEventListener("change", (e) => {
      produtoCategoryFilter = e.target.value;
      renderProdutosTable();
    });
  }

  if (statusFilter) {
    statusFilter.addEventListener("change", (e) => {
      produtoStatusFilter = e.target.value;
      renderProdutosTable();
    });
  }

  // Open modal button
  const openNewBtn = document.getElementById("open-new-produto-btn");
  if (openNewBtn) {
    openNewBtn.addEventListener("click", () => {
      openProdutoModal(null);
    });
  }

  // Close modal buttons
  const closeBtn = document.getElementById("close-produto-modal-btn");
  const cancelBtn = document.getElementById("cancel-produto-modal-btn");
  if (closeBtn) closeBtn.addEventListener("click", closeProdutoModal);
  if (cancelBtn) cancelBtn.addEventListener("click", closeProdutoModal);

  // Switch "É uma Bebida?" dynamic toggle
  const isBebidaCheckbox = document.getElementById("produto-isBebida");
  const bebidaFields = document.getElementById("bebida-specific-fields");
  const tamanhoInput = document.getElementById("produto-tamanhoML");
  const marcaInput = document.getElementById("produto-marca");

  if (isBebidaCheckbox && bebidaFields) {
    isBebidaCheckbox.addEventListener("change", (e) => {
      if (e.target.checked) {
        bebidaFields.classList.remove("hidden");
        tamanhoInput.setAttribute("required", "required");
        marcaInput.setAttribute("required", "required");
      } else {
        bebidaFields.classList.add("hidden");
        tamanhoInput.removeAttribute("required");
        marcaInput.removeAttribute("required");
      }
    });
  }

  // Live image preview
  const imageUrlInput = document.getElementById("produto-imagemUrl");
  const imagePreview = document.getElementById("produto-preview-img");
  if (imageUrlInput && imagePreview) {
    imageUrlInput.addEventListener("input", (e) => {
      const url = e.target.value.trim();
      if (url) {
        imagePreview.src = url;
        imagePreview.onerror = () => {
          imagePreview.src = getPlaceholderImage("Preview", isBebidaCheckbox?.checked);
        };
      } else {
        imagePreview.src = getPlaceholderImage("Preview", isBebidaCheckbox?.checked);
      }
    });
  }

  // Form Submit (Create or Update)
  const form = document.getElementById("produto-form");
  if (form) {
    form.addEventListener("submit", handleProdutoSubmit);
  }

  // Setup Batch Actions Events
  setupBatchActionsEvents();
}

function setupBatchActionsEvents() {
  // Master checkbox
  const selectAll = document.getElementById("select-all-produtos");
  if (selectAll) {
    selectAll.addEventListener("change", (e) => {
      const isChecked = e.target.checked;
      const filtered = getFilteredProdutos();
      filtered.forEach((p) => {
        if (isChecked) {
          selectedProdutoIds.add(p.id);
        } else {
          selectedProdutoIds.delete(p.id);
        }
      });
      document.querySelectorAll(".produto-row-checkbox").forEach((cb) => {
        const id = Number(cb.getAttribute("data-produto-id"));
        cb.checked = selectedProdutoIds.has(id);
        const tr = cb.closest("tr");
        if (tr) {
          if (cb.checked) tr.classList.add("bg-amber-500/10");
          else tr.classList.remove("bg-amber-500/10");
        }
      });
      updateBatchActionsBar(filtered);
    });
  }

  // Deselect all button
  const deselectBtn = document.getElementById("batch-deselect-all-btn");
  if (deselectBtn) {
    deselectBtn.addEventListener("click", () => {
      selectedProdutoIds.clear();
      document.querySelectorAll(".produto-row-checkbox").forEach((cb) => {
        cb.checked = false;
        const tr = cb.closest("tr");
        if (tr) tr.classList.remove("bg-amber-500/10");
      });
      updateBatchActionsBar();
    });
  }

  // Batch Alterar Preço
  const alterarPrecoBtn = document.getElementById("batch-alterar-preco-btn");
  const modal = document.getElementById("batch-preco-modal");
  const form = document.getElementById("batch-preco-form");
  const closeBtn = document.getElementById("close-batch-preco-modal-btn");
  const cancelBtn = document.getElementById("cancel-batch-preco-modal-btn");
  const descEl = document.getElementById("batch-preco-desc");
  const inputPreco = document.getElementById("batch-novo-preco");

  if (alterarPrecoBtn && modal) {
    alterarPrecoBtn.addEventListener("click", () => {
      if (selectedProdutoIds.size === 0) return;
      if (descEl) {
        descEl.textContent = `Informe o novo valor a ser aplicado para os ${selectedProdutoIds.size} produto(s) selecionado(s).`;
      }
      if (form) form.reset();
      modal.classList.remove("hidden");
      if (inputPreco) inputPreco.focus();
    });
  }

  const closePrecoModal = () => {
    if (modal) modal.classList.add("hidden");
  };

  if (closeBtn) closeBtn.addEventListener("click", closePrecoModal);
  if (cancelBtn) cancelBtn.addEventListener("click", closePrecoModal);

  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      const novoPreco = parseFloat(inputPreco.value);
      if (isNaN(novoPreco) || novoPreco <= 0) {
        toast.warning("Informe um preço válido maior que zero.");
        return;
      }

      closePrecoModal();
      await executeBatchUpdate({ preco: novoPreco }, `Preço alterado com sucesso em`);
    });
  }

  // Batch Ativar
  const ativarBtn = document.getElementById("batch-ativar-btn");
  if (ativarBtn) {
    ativarBtn.addEventListener("click", async () => {
      if (selectedProdutoIds.size === 0) return;
      const count = selectedProdutoIds.size;
      const confirmed = await openConfirmDialog(
        "Ativar em Lote",
        `Deseja ativar os ${count} produto(s) selecionado(s)?`,
        "Ativar Todos",
        "success"
      );
      if (confirmed) {
        await executeBatchUpdate({ ativo: true }, `Ativado(s) com sucesso`);
      }
    });
  }

  // Batch Inativar
  const inativarBtn = document.getElementById("batch-inativar-btn");
  if (inativarBtn) {
    inativarBtn.addEventListener("click", async () => {
      if (selectedProdutoIds.size === 0) return;
      const count = selectedProdutoIds.size;
      const confirmed = await openConfirmDialog(
        "Inativar em Lote",
        `Deseja inativar os ${count} produto(s) selecionado(s)? Eles não aparecerão mais no cardápio público dos clientes.`,
        "Inativar Todos",
        "danger"
      );
      if (confirmed) {
        await executeBatchUpdate({ ativo: false }, `Inativado(s) com sucesso`);
      }
    });
  }
}

function openProdutoModal(productId = null) {
  editingProdutoId = productId;
  const modal = document.getElementById("produto-modal");
  const form = document.getElementById("produto-form");
  const titleEl = document.getElementById("produto-modal-title");
  const ativoContainer = document.getElementById("produto-ativo-container");
  const isBebidaCheckbox = document.getElementById("produto-isBebida");
  const bebidaFields = document.getElementById("bebida-specific-fields");
  const tamanhoInput = document.getElementById("produto-tamanhoML");
  const marcaInput = document.getElementById("produto-marca");
  const imagePreview = document.getElementById("produto-preview-img");

  form.reset();

  if (productId) {
    // Editing existing product
    const product = produtosList.find((p) => p.id === productId);
    if (!product) return;

    titleEl.textContent = "Editar Produto / Bebida";
    if (ativoContainer) ativoContainer.classList.remove("hidden");

    form.nome.value = product.nome || "";
    form.descricao.value = product.descricao || "";
    form.preco.value = product.preco || "";
    form.imagemUrl.value = product.imagemUrl || "";
    form.categoriaId.value = product.categoriaId || "";
    if (form.ativo) form.ativo.checked = product.ativo;

    const isBebida = product.tipo === "BEBIDA";
    isBebidaCheckbox.checked = isBebida;

    if (isBebida) {
      bebidaFields.classList.remove("hidden");
      tamanhoInput.value = product.tamanhoML || "";
      marcaInput.value = product.marca || "";
      tamanhoInput.setAttribute("required", "required");
      marcaInput.setAttribute("required", "required");
    } else {
      bebidaFields.classList.add("hidden");
      tamanhoInput.removeAttribute("required");
      marcaInput.removeAttribute("required");
    }

    if (imagePreview) {
      imagePreview.src = product.imagemUrl || getPlaceholderImage(product.nome, isBebida);
    }
  } else {
    // Creating new product
    titleEl.textContent = "Cadastrar Novo Produto";
    if (ativoContainer) ativoContainer.classList.add("hidden");

    isBebidaCheckbox.checked = false;
    bebidaFields.classList.add("hidden");
    tamanhoInput.removeAttribute("required");
    marcaInput.removeAttribute("required");

    if (imagePreview) {
      imagePreview.src = getPlaceholderImage("BeerGod", false);
    }
  }

  if (modal) modal.classList.remove("hidden");
}

function closeProdutoModal() {
  const modal = document.getElementById("produto-modal");
  if (modal) modal.classList.add("hidden");
  editingProdutoId = null;
}

async function handleProdutoSubmit(e) {
  e.preventDefault();
  const form = e.target;
  const submitBtn = form.querySelector("button[type='submit']");
  const originalBtnText = submitBtn.innerHTML;

  const isBebida = form.isBebida.checked;

  const produtoData = {
    nome: form.nome.value,
    descricao: form.descricao.value,
    preco: form.preco.value,
    imagemUrl: form.imagemUrl.value,
    categoriaId: form.categoriaId.value,
    isBebida: isBebida,
    tamanhoML: isBebida ? form.tamanhoML.value : null,
    marca: isBebida ? form.marca.value : null,
    ativo: form.ativo ? form.ativo.checked : true,
  };

  try {
    submitBtn.disabled = true;
    submitBtn.innerHTML = "Salvando...";

    if (editingProdutoId) {
      await atualizarProduto(editingProdutoId, produtoData);
      toast.success("Produto atualizado com sucesso!");
    } else {
      await criarProduto(produtoData);
      toast.success("Produto cadastrado com sucesso!");
    }

    closeProdutoModal();
    await loadProdutosData();
  } catch (err) {
    console.error("Erro ao salvar produto:", err);
    toast.error(err.message || "Erro ao salvar produto.");
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = originalBtnText;
  }
}

// Global actions for Products
window.openEditProdutoModal = function (id) {
  openProdutoModal(id);
};

window.confirmInativarProduto = async function (id) {
  const product = produtosList.find((p) => p.id === id);
  const nome = product ? product.nome : "este produto";

  const confirmed = await openConfirmDialog(
    "Inativar Produto",
    `Tem certeza que deseja inativar o produto "${nome}"? Ele não aparecerá mais no cardápio público dos clientes.`,
    "Inativar",
    "danger"
  );

  if (confirmed) {
    try {
      await inativarProduto(id);
      toast.success(`Produto "${nome}" inativado com sucesso.`);
      await loadProdutosData();
    } catch (err) {
      toast.error(err.message || "Erro ao inativar produto.");
    }
  }
};

window.confirmReativarProduto = async function (id) {
  const product = produtosList.find((p) => p.id === id);
  if (!product) return;

  try {
    const isBebida = product.tipo === "BEBIDA";
    await atualizarProduto(id, {
      nome: product.nome,
      descricao: product.descricao,
      preco: product.preco,
      imagemUrl: product.imagemUrl,
      categoriaId: product.categoriaId,
      isBebida: isBebida,
      tamanhoML: isBebida ? product.tamanhoML : null,
      marca: isBebida ? product.marca : null,
      ativo: true,
    });
    toast.success(`Produto "${product.nome}" reativado com sucesso!`);
    await loadProdutosData();
  } catch (err) {
    toast.error(err.message || "Erro ao reativar produto.");
  }
};

/* ==========================================================================
   ABA 2: CATEGORIAS
   ========================================================================== */
async function loadCategoriasData() {
  const tableBody = document.getElementById("categorias-table-body");
  if (tableBody) {
    tableBody.innerHTML = `<tr><td colspan="4" class="text-center py-8 text-slate-400">Carregando categorias...</td></tr>`;
  }

  try {
    const [cats, prods] = await Promise.all([getCategorias(false), getProdutos(false)]);
    categoriasList = Array.isArray(cats) ? cats : [];
    produtosList = Array.isArray(prods) ? prods : [];

    renderCategoriasTable();
    updateCategoriasCountBadge();
  } catch (err) {
    console.error("Erro ao carregar categorias:", err);
    toast.error(err.message || "Erro ao carregar categorias.");
  }
}

function updateCategoriasCountBadge() {
  const badge = document.getElementById("badge-count-categorias");
  if (badge) badge.textContent = categoriasList.length;
}

function renderCategoriasTable() {
  const tableBody = document.getElementById("categorias-table-body");
  if (!tableBody) return;

  const filtered = categoriasList.filter((cat) => {
    return categoriaSearch === "" || cat.nome.toLowerCase().includes(categoriaSearch);
  });

  if (filtered.length === 0) {
    tableBody.innerHTML = `
      <tr>
        <td colspan="4" class="text-center py-10 text-slate-400">
          Nenhuma categoria cadastrada.
        </td>
      </tr>
    `;
    return;
  }

  tableBody.innerHTML = filtered
    .map((cat) => {
      const prodCount = produtosList.filter((p) => p.categoriaId === cat.id).length;

      return `
      <tr class="border-b border-slate-700/60 hover:bg-slate-800/50 transition-colors">
        <td class="py-3.5 px-4 font-mono text-xs text-slate-400">#${cat.id}</td>
        <td class="py-3.5 px-4 font-semibold text-slate-100">${escapeHtml(cat.nome)}</td>
        <td class="py-3.5 px-4">
          <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-800 text-slate-300 border border-slate-700">
            ${prodCount} ${prodCount === 1 ? "produto" : "produtos"}
          </span>
        </td>
        <td class="py-3.5 px-4">
          ${
            cat.ativo
              ? `<span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                  Ativa
                </span>`
              : `<span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-rose-400"></span>
                  Inativa
                </span>`
          }
        </td>
        <td class="py-3.5 px-4 text-right">
          <div class="flex items-center justify-end gap-2">
            <button
              type="button"
              onclick="window.openEditCategoriaModal(${cat.id})"
              class="p-1.5 rounded-lg text-slate-300 hover:text-amber-400 hover:bg-slate-700/60 transition-colors"
              title="Editar Categoria"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>

            ${
              cat.ativo
                ? `
              <button
                type="button"
                onclick="window.confirmInativarCategoria(${cat.id})"
                class="p-1.5 rounded-lg text-rose-400 hover:text-rose-300 hover:bg-rose-500/10 transition-colors"
                title="Inativar Categoria"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>`
                : `
              <button
                type="button"
                onclick="window.confirmReativarCategoria(${cat.id})"
                class="p-1.5 rounded-lg text-emerald-400 hover:text-emerald-300 hover:bg-emerald-500/10 transition-colors"
                title="Reativar Categoria"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </button>`
            }
          </div>
        </td>
      </tr>
    `;
    })
    .join("");
}

function setupCategoryFormEvents() {
  // Search
  const searchInput = document.getElementById("categoria-search-input");
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      categoriaSearch = e.target.value.trim().toLowerCase();
      renderCategoriasTable();
    });
  }

  // Quick Create Form
  const quickForm = document.getElementById("quick-categoria-form");
  if (quickForm) {
    quickForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const nomeInput = quickForm.nome;
      const nome = nomeInput.value.trim();
      if (!nome) {
        toast.warning("Digite o nome da nova categoria.");
        return;
      }

      try {
        await criarCategoria({ nome });
        toast.success(`Categoria "${nome}" criada com sucesso!`);
        nomeInput.value = "";
        await loadCategoriasData();
      } catch (err) {
        toast.error(err.message || "Erro ao criar categoria.");
      }
    });
  }

  // Edit Modal events
  const closeBtn = document.getElementById("close-categoria-modal-btn");
  const cancelBtn = document.getElementById("cancel-categoria-modal-btn");
  if (closeBtn) closeBtn.addEventListener("click", closeCategoriaModal);
  if (cancelBtn) cancelBtn.addEventListener("click", closeCategoriaModal);

  const editForm = document.getElementById("categoria-edit-form");
  if (editForm) {
    editForm.addEventListener("submit", handleCategoriaEditSubmit);
  }
}

function openEditCategoriaModal(id) {
  const cat = categoriasList.find((c) => c.id === id);
  if (!cat) return;

  editingCategoriaId = id;
  const modal = document.getElementById("categoria-modal");
  const form = document.getElementById("categoria-edit-form");

  if (form) {
    form.nome.value = cat.nome || "";
    form.ativo.checked = cat.ativo;
  }

  if (modal) modal.classList.remove("hidden");
}

function closeCategoriaModal() {
  const modal = document.getElementById("categoria-modal");
  if (modal) modal.classList.add("hidden");
  editingCategoriaId = null;
}

async function handleCategoriaEditSubmit(e) {
  e.preventDefault();
  const form = e.target;
  const nome = form.nome.value.trim();
  const ativo = form.ativo.checked;

  if (!nome) {
    toast.warning("O nome da categoria é obrigatório.");
    return;
  }

  try {
    await atualizarCategoria(editingCategoriaId, { nome, ativo });
    toast.success("Categoria atualizada com sucesso!");
    closeCategoriaModal();
    await loadCategoriasData();
  } catch (err) {
    toast.error(err.message || "Erro ao atualizar categoria.");
  }
}

// Global actions for Categories
window.openEditCategoriaModal = function (id) {
  openEditCategoriaModal(id);
};

window.confirmInativarCategoria = async function (id) {
  const cat = categoriasList.find((c) => c.id === id);
  const nome = cat ? cat.nome : "esta categoria";

  const confirmed = await openConfirmDialog(
    "Inativar Categoria",
    `Tem certeza que deseja inativar a categoria "${nome}"?`,
    "Inativar",
    "danger"
  );

  if (confirmed) {
    try {
      await inativarCategoria(id);
      toast.success(`Categoria "${nome}" inativada com sucesso.`);
      await loadCategoriasData();
    } catch (err) {
      toast.error(err.message || "Erro ao inativar categoria.");
    }
  }
};

window.confirmReativarCategoria = async function (id) {
  const cat = categoriasList.find((c) => c.id === id);
  if (!cat) return;

  try {
    await atualizarCategoria(id, { nome: cat.nome, ativo: true });
    toast.success(`Categoria "${cat.nome}" reativada com sucesso!`);
    await loadCategoriasData();
  } catch (err) {
    toast.error(err.message || "Erro ao reativar categoria.");
  }
};

/* ==========================================================================
   ABA 3: ADMINISTRADORES
   ========================================================================== */
async function loadAdministradoresData() {
  const tableBody = document.getElementById("admins-table-body");
  if (tableBody) {
    tableBody.innerHTML = `<tr><td colspan="5" class="text-center py-8 text-slate-400">Carregando lista de administradores...</td></tr>`;
  }

  try {
    const admins = await getAdministradores();
    administradoresList = Array.isArray(admins) ? admins : [];

    renderAdministradoresTable();
    updateAdministradoresCountBadge();
  } catch (err) {
    console.error("Erro ao carregar administradores:", err);
    toast.error(err.message || "Erro ao carregar administradores.");
  }
}

function updateAdministradoresCountBadge() {
  const badge = document.getElementById("badge-count-administradores");
  if (badge) badge.textContent = administradoresList.length;
}

function renderAdministradoresTable() {
  const tableBody = document.getElementById("admins-table-body");
  if (!tableBody) return;

  const currentLoggedId = currentAdmin ? currentAdmin.id : null;

  const filtered = administradoresList.filter((admin) => {
    return (
      adminSearch === "" ||
      admin.nome.toLowerCase().includes(adminSearch) ||
      admin.email.toLowerCase().includes(adminSearch)
    );
  });

  if (filtered.length === 0) {
    tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="text-center py-10 text-slate-400">
          Nenhum administrador encontrado.
        </td>
      </tr>
    `;
    return;
  }

  tableBody.innerHTML = filtered
    .map((admin) => {
      const isSelf = currentLoggedId && admin.id === currentLoggedId;

      return `
      <tr class="border-b border-slate-700/60 hover:bg-slate-800/50 transition-colors ${
        isSelf ? "bg-amber-500/5" : ""
      }">
        <td class="py-3.5 px-4 font-mono text-xs text-slate-400">#${admin.id}</td>
        <td class="py-3.5 px-4">
          <div class="flex items-center gap-2">
            <span class="font-bold text-slate-100">${escapeHtml(admin.nome)}</span>
            ${
              isSelf
                ? `<span class="px-2 py-0.5 rounded text-[10px] font-extrabold bg-amber-500 text-slate-950 uppercase">Você</span>`
                : ""
            }
          </div>
        </td>
        <td class="py-3.5 px-4 font-mono text-xs text-slate-300">${escapeHtml(admin.email)}</td>
        <td class="py-3.5 px-4">
          ${
            admin.ativo
              ? `<span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                  Ativo
                </span>`
              : `<span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/30">
                  <span class="w-1.5 h-1.5 rounded-full bg-rose-400"></span>
                  Inativo
                </span>`
          }
        </td>
        <td class="py-3.5 px-4 text-right">
          <div class="flex items-center justify-end gap-2">
            <button
              type="button"
              onclick="window.openEditAdminModal(${admin.id})"
              class="p-1.5 rounded-lg text-slate-300 hover:text-amber-400 hover:bg-slate-700/60 transition-colors"
              title="Editar Administrador"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>

            ${
              admin.ativo
                ? `
              <button
                type="button"
                onclick="window.confirmInativarAdmin(${admin.id})"
                class="p-1.5 rounded-lg text-rose-400 hover:text-rose-300 hover:bg-rose-500/10 transition-colors"
                title="Inativar Administrador"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>`
                : `
              <button
                type="button"
                onclick="window.confirmReativarAdmin(${admin.id})"
                class="p-1.5 rounded-lg text-emerald-400 hover:text-emerald-300 hover:bg-emerald-500/10 transition-colors"
                title="Reativar Administrador"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </button>`
            }
          </div>
        </td>
      </tr>
    `;
    })
    .join("");
}

function setupAdminFormEvents() {
  const searchInput = document.getElementById("admin-search-input");
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      adminSearch = e.target.value.trim().toLowerCase();
      renderAdministradoresTable();
    });
  }

  // Open New Admin Modal
  const openNewBtn = document.getElementById("open-new-admin-btn");
  if (openNewBtn) {
    openNewBtn.addEventListener("click", () => {
      openAdminModal(null);
    });
  }

  // Close Modal Buttons
  const closeBtn = document.getElementById("close-admin-modal-btn");
  const cancelBtn = document.getElementById("cancel-admin-modal-btn");
  if (closeBtn) closeBtn.addEventListener("click", closeAdminModal);
  if (cancelBtn) cancelBtn.addEventListener("click", closeAdminModal);

  // Form Submit
  const form = document.getElementById("admin-form");
  if (form) {
    form.addEventListener("submit", handleAdminSubmit);
  }
}

function openAdminModal(adminId = null) {
  editingAdminId = adminId;
  const modal = document.getElementById("admin-modal");
  const form = document.getElementById("admin-form");
  const titleEl = document.getElementById("admin-modal-title");
  const senhaInput = document.getElementById("admin-senha");
  const senhaHint = document.getElementById("admin-senha-hint");
  const ativoContainer = document.getElementById("admin-ativo-container");

  form.reset();

  if (adminId) {
    const admin = administradoresList.find((a) => a.id === adminId);
    if (!admin) return;

    titleEl.textContent = "Editar Administrador";
    form.nome.value = admin.nome;
    form.email.value = admin.email;
    form.ativo.checked = admin.ativo;

    senhaInput.removeAttribute("required");
    senhaInput.placeholder = "Deixe em branco para manter a senha atual";
    if (senhaHint) senhaHint.textContent = "Deixe em branco para manter a senha atual (mínimo 6 dígitos ao alterar).";
    if (ativoContainer) ativoContainer.classList.remove("hidden");
  } else {
    titleEl.textContent = "Cadastrar Novo Administrador";
    senhaInput.setAttribute("required", "required");
    senhaInput.placeholder = "Senha de acesso (mínimo 6 caracteres)";
    if (senhaHint) senhaHint.textContent = "A senha deve conter no mínimo 6 caracteres.";
    if (ativoContainer) ativoContainer.classList.add("hidden");
  }

  if (modal) modal.classList.remove("hidden");
}

function closeAdminModal() {
  const modal = document.getElementById("admin-modal");
  if (modal) modal.classList.add("hidden");
  editingAdminId = null;
}

async function handleAdminSubmit(e) {
  e.preventDefault();
  const form = e.target;
  const submitBtn = form.querySelector("button[type='submit']");
  const originalBtnText = submitBtn.innerHTML;

  const nome = form.nome.value.trim();
  const email = form.email.value.trim();
  const senha = form.senha.value;
  const ativo = form.ativo ? form.ativo.checked : true;

  if (!nome || !email) {
    toast.warning("Nome e e-mail são obrigatórios.");
    return;
  }

  if (!editingAdminId && (!senha || senha.length < 6)) {
    toast.warning("A senha deve ter no mínimo 6 caracteres.");
    return;
  }

  if (editingAdminId && senha && senha.length < 6) {
    toast.warning("A nova senha deve ter no mínimo 6 caracteres.");
    return;
  }

  try {
    submitBtn.disabled = true;
    submitBtn.innerHTML = "Salvando...";

    if (editingAdminId) {
      await atualizarAdministrador(editingAdminId, {
        nome,
        email,
        senha: senha && senha.trim().length > 0 ? senha : null,
        ativo,
      });

      // If updating current logged in admin, update stored session
      if (currentAdmin && currentAdmin.id === editingAdminId) {
        setStoredAdmin({ ...currentAdmin, nome, email, ativo });
        showDashboardView();
      }

      toast.success("Administrador atualizado com sucesso!");
    } else {
      await criarAdministrador({ nome, email, senha });
      toast.success("Administrador cadastrado com sucesso!");
    }

    closeAdminModal();
    await loadAdministradoresData();
  } catch (err) {
    console.error("Erro ao salvar administrador:", err);
    toast.error(err.message || "Erro ao salvar administrador.");
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = originalBtnText;
  }
}

// Global actions for Administrators
window.openEditAdminModal = function (id) {
  openAdminModal(id);
};

window.confirmInativarAdmin = async function (id) {
  const admin = administradoresList.find((a) => a.id === id);
  const nome = admin ? admin.nome : "este administrador";
  const isSelf = currentAdmin && currentAdmin.id === id;

  if (isSelf) {
    const warningConfirmed = await openConfirmDialog(
      "Atenção: Inativar a própria conta",
      "Você está prestes a inativar a conta que está usando no momento! Se continuar, perderá acesso ao painel após sair.",
      "Inativar mesmo assim",
      "danger"
    );
    if (!warningConfirmed) return;
  } else {
    const confirmed = await openConfirmDialog(
      "Inativar Administrador",
      `Tem certeza que deseja inativar o administrador "${nome}"?`,
      "Inativar",
      "danger"
    );
    if (!confirmed) return;
  }

  try {
    await inativarAdministrador(id);
    toast.success(`Administrador "${nome}" inativado com sucesso.`);
    await loadAdministradoresData();
  } catch (err) {
    toast.error(err.message || "Erro ao inativar administrador.");
  }
};

window.confirmReativarAdmin = async function (id) {
  const admin = administradoresList.find((a) => a.id === id);
  if (!admin) return;

  try {
    await atualizarAdministrador(id, {
      nome: admin.nome,
      email: admin.email,
      senha: null,
      ativo: true,
    });
    toast.success(`Administrador "${admin.nome}" reativado com sucesso!`);
    await loadAdministradoresData();
  } catch (err) {
    toast.error(err.message || "Erro ao reativar administrador.");
  }
};

/* ==========================================================================
   Custom Confirmation Dialog (replaces native alert/confirm)
   ========================================================================== */
function openConfirmDialog(title, message, confirmText = "Confirmar", theme = "danger") {
  return new Promise((resolve) => {
    const modalBackdrop = document.createElement("div");
    modalBackdrop.className =
      "fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 modal-backdrop";

    const confirmColorClass =
      theme === "danger"
        ? "bg-rose-600 hover:bg-rose-500 text-white"
        : "bg-amber-500 hover:bg-amber-400 text-slate-950";

    modalBackdrop.innerHTML = `
      <div class="relative w-full max-w-md overflow-hidden rounded-2xl bg-slate-900 border border-slate-700 shadow-2xl p-6">
        <h3 class="text-lg font-bold text-slate-100">${escapeHtml(title)}</h3>
        <p class="mt-2 text-sm text-slate-300 leading-relaxed">${escapeHtml(message)}</p>
        <div class="mt-6 flex items-center justify-end gap-3">
          <button
            type="button"
            id="dialog-cancel-btn"
            class="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold transition-all border border-slate-700"
          >
            Cancelar
          </button>
          <button
            type="button"
            id="dialog-confirm-btn"
            class="px-4 py-2 rounded-xl ${confirmColorClass} text-xs font-bold transition-all shadow-md"
          >
            ${escapeHtml(confirmText)}
          </button>
        </div>
      </div>
    `;

    document.body.appendChild(modalBackdrop);

    const cleanup = (result) => {
      if (modalBackdrop.parentElement) {
        modalBackdrop.parentElement.removeChild(modalBackdrop);
      }
      resolve(result);
    };

    modalBackdrop.querySelector("#dialog-cancel-btn").addEventListener("click", () => cleanup(false));
    modalBackdrop.querySelector("#dialog-confirm-btn").addEventListener("click", () => cleanup(true));
  });
}

// Auto-run on DOM ready
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initAdmin);
} else {
  initAdmin();
}

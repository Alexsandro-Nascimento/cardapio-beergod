import { getCategorias, getProdutos } from "./api.js";
import { formatBRL, escapeHtml, getPlaceholderImage } from "./utils.js";
import { toast } from "./toast.js";

// State
let categories = [];
let products = [];
let selectedCategoryId = "all";
let searchTerm = "";
let isLoading = true;

// DOM Elements
const categoryFiltersContainer = document.getElementById("category-filters");
const productsGrid = document.getElementById("products-grid");
const searchInput = document.getElementById("search-input");
const clearSearchBtn = document.getElementById("clear-search-btn");
const resultsCountEl = document.getElementById("results-count");
const emptyStateEl = document.getElementById("empty-state");
const errorBannerEl = document.getElementById("error-banner");
const retryBtn = document.getElementById("retry-btn");

/**
 * Initialize Menu Page
 */
export async function initMenu() {
  setupEventListeners();
  await loadData();
}

/**
 * Setup Event Listeners
 */
function setupEventListeners() {
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      searchTerm = e.target.value.trim().toLowerCase();
      if (clearSearchBtn) {
        if (searchTerm.length > 0) {
          clearSearchBtn.classList.remove("hidden");
        } else {
          clearSearchBtn.classList.add("hidden");
        }
      }
      renderProducts();
    });
  }

  if (clearSearchBtn) {
    clearSearchBtn.addEventListener("click", () => {
      if (searchInput) {
        searchInput.value = "";
        searchTerm = "";
        clearSearchBtn.classList.add("hidden");
        renderProducts();
        searchInput.focus();
      }
    });
  }

  if (retryBtn) {
    retryBtn.addEventListener("click", () => {
      loadData();
    });
  }

  // Global handler for image fallback
  window.__beergodImageFallback = function (img, isBebida) {
    img.onerror = null;
    img.src = getPlaceholderImage("BeerGod", isBebida);
  };
}

/**
 * Load Categories and Products from API
 */
async function loadData() {
  isLoading = true;
  renderSkeletons();
  if (errorBannerEl) errorBannerEl.classList.add("hidden");

  try {
    const [fetchedCategories, fetchedProducts] = await Promise.all([
      getCategorias(true),
      getProdutos(true),
    ]);

    categories = Array.isArray(fetchedCategories) ? fetchedCategories : [];
    products = Array.isArray(fetchedProducts) ? fetchedProducts : [];

    renderCategoryFilters();
    renderProducts();
  } catch (error) {
    console.error("Erro ao carregar cardápio:", error);
    if (errorBannerEl) {
      errorBannerEl.classList.remove("hidden");
      const msgEl = errorBannerEl.querySelector(".error-message");
      if (msgEl) msgEl.textContent = error.message;
    }
    toast.error(error.message);
    if (productsGrid) productsGrid.innerHTML = "";
    if (resultsCountEl) resultsCountEl.textContent = "0 itens encontrados";
  } finally {
    isLoading = false;
  }
}

/**
 * Render Horizontal Category Filter Chips
 */
function renderCategoryFilters() {
  if (!categoryFiltersContainer) return;

  categoryFiltersContainer.innerHTML = "";

  // "Todos" button
  const allBtn = document.createElement("button");
  allBtn.type = "button";
  allBtn.className = getCategoryButtonClass(selectedCategoryId === "all");
  allBtn.innerHTML = `
    <span class="flex items-center gap-2">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
      </svg>
      <span>Todos</span>
      <span class="text-xs px-2 py-0.5 rounded-full ${
        selectedCategoryId === "all" ? "bg-amber-500/30 text-amber-200" : "bg-slate-700/60 text-slate-400"
      }">${products.length}</span>
    </span>
  `;
  allBtn.addEventListener("click", () => {
    selectedCategoryId = "all";
    renderCategoryFilters();
    renderProducts();
  });
  categoryFiltersContainer.appendChild(allBtn);

  // Each active category button
  categories.forEach((cat) => {
    const isSelected = String(selectedCategoryId) === String(cat.id);
    const count = products.filter((p) => String(p.categoriaId) === String(cat.id)).length;

    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = getCategoryButtonClass(isSelected);
    btn.innerHTML = `
      <span class="flex items-center gap-2 whitespace-nowrap">
        <span>${escapeHtml(cat.nome)}</span>
        <span class="text-xs px-2 py-0.5 rounded-full ${
          isSelected ? "bg-amber-500/30 text-amber-200" : "bg-slate-700/60 text-slate-400"
        }">${count}</span>
      </span>
    `;

    btn.addEventListener("click", () => {
      selectedCategoryId = String(cat.id);
      renderCategoryFilters();
      renderProducts();
      // Scroll chip smoothly into view
      btn.scrollIntoView({ behavior: "smooth", inline: "center", block: "nearest" });
    });

    categoryFiltersContainer.appendChild(btn);
  });
}

function getCategoryButtonClass(isSelected) {
  const base =
    "px-4 py-2 rounded-full text-sm font-medium transition-all duration-200 flex-shrink-0 cursor-pointer shadow-sm";
  if (isSelected) {
    return `${base} bg-gradient-to-r from-amber-500 to-amber-600 text-slate-950 font-semibold ring-2 ring-amber-400/50 shadow-amber-500/20 shadow-md transform scale-[1.02]`;
  }
  return `${base} bg-slate-800/90 text-slate-300 hover:bg-slate-700 hover:text-white border border-slate-700/60`;
}

/**
 * Filter and Render Products
 */
function renderProducts() {
  if (!productsGrid) return;

  const filtered = products.filter((prod) => {
    // Category match
    const matchCat =
      selectedCategoryId === "all" || String(prod.categoriaId) === String(selectedCategoryId);

    // Search query match in name or description
    const matchSearch =
      searchTerm === "" ||
      (prod.nome && prod.nome.toLowerCase().includes(searchTerm)) ||
      (prod.descricao && prod.descricao.toLowerCase().includes(searchTerm)) ||
      (prod.marca && prod.marca.toLowerCase().includes(searchTerm)) ||
      (prod.categoriaNome && prod.categoriaNome.toLowerCase().includes(searchTerm));

    return matchCat && matchSearch;
  });

  // Update counter
  if (resultsCountEl) {
    resultsCountEl.textContent = `${filtered.length} ${
      filtered.length === 1 ? "item disponível" : "itens disponíveis"
    }`;
  }

  // Empty state handling
  if (filtered.length === 0) {
    productsGrid.innerHTML = "";
    if (emptyStateEl) {
      emptyStateEl.classList.remove("hidden");
      const emptyMsg = emptyStateEl.querySelector(".empty-message");
      if (emptyMsg) {
        if (searchTerm) {
          emptyMsg.textContent = `Nenhum produto encontrado para "${searchTerm}".`;
        } else {
          emptyMsg.textContent = "Nenhum produto cadastrado nesta categoria.";
        }
      }
    }
    return;
  }

  if (emptyStateEl) {
    emptyStateEl.classList.add("hidden");
  }

  // Render cards
  productsGrid.innerHTML = filtered.map((prod) => createProductCardHtml(prod)).join("");
}

/**
 * Generate Product Card HTML
 */
function createProductCardHtml(product) {
  const isBebida = product.tipo === "BEBIDA";
  const imageSource = product.imagemUrl && product.imagemUrl.trim().length > 0
    ? escapeHtml(product.imagemUrl)
    : getPlaceholderImage(product.nome, isBebida);

  // Drink extra badges
  let drinkBadgesHtml = "";
  if (isBebida) {
    drinkBadgesHtml = `
      <div class="flex flex-wrap items-center gap-1.5 mt-2">
        ${
          product.marca
            ? `
          <span class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-md text-xs font-semibold bg-amber-500/10 text-amber-300 border border-amber-500/30">
            <svg class="w-3.5 h-3.5 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
            </svg>
            ${escapeHtml(product.marca)}
          </span>`
            : ""
        }
        ${
          product.tamanhoML
            ? `
          <span class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-md text-xs font-semibold bg-sky-500/10 text-sky-300 border border-sky-500/30">
            <svg class="w-3.5 h-3.5 text-sky-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
            ${product.tamanhoML}ml
          </span>`
            : ""
        }
      </div>
    `;
  }

  return `
    <article class="product-card group relative flex flex-col overflow-hidden rounded-2xl bg-slate-800/90 border border-slate-700/80 shadow-lg backdrop-blur-sm">
      <!-- Image Container -->
      <div class="relative w-full aspect-[4/3] overflow-hidden bg-slate-900">
        <img
          src="${imageSource}"
          alt="${escapeHtml(product.nome)}"
          loading="lazy"
          onerror="window.__beergodImageFallback(this, ${isBebida})"
          class="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500 ease-out"
        />

        <!-- Category Tag Badge -->
        <div class="absolute top-3 left-3">
          <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-slate-900/85 text-amber-400 border border-amber-500/30 backdrop-blur-md shadow-sm">
            ${escapeHtml(product.categoriaNome || "Geral")}
          </span>
        </div>

        ${
          isBebida
            ? `
          <div class="absolute top-3 right-3">
            <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-500 text-slate-950 shadow-md">
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
              </svg>
              Bebida
            </span>
          </div>`
            : ""
        }
      </div>

      <!-- Content Container -->
      <div class="flex flex-1 flex-col justify-between p-5">
        <div>
          <!-- Title -->
          <h3 class="text-lg font-bold text-slate-100 group-hover:text-amber-400 transition-colors line-clamp-1">
            ${escapeHtml(product.nome)}
          </h3>

          <!-- Drink Badges -->
          ${drinkBadgesHtml}

          <!-- Description -->
          <p class="mt-2.5 text-sm text-slate-300/80 leading-relaxed line-clamp-2 min-h-[2.5rem]">
            ${escapeHtml(product.descricao) || "Sem descrição disponível."}
          </p>
        </div>

        <!-- Footer / Price -->
        <div class="mt-4 pt-4 border-t border-slate-700/50 flex items-center justify-between">
          <div>
            <span class="text-xs uppercase tracking-wider text-slate-400 font-medium block">Preço</span>
            <span class="text-xl font-extrabold text-amber-400 font-serif-brand">
              ${formatBRL(product.preco)}
            </span>
          </div>

          <button
            type="button"
            onclick="window.showProductDetails(${product.id})"
            class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-200 bg-slate-700/60 hover:bg-amber-500 hover:text-slate-950 transition-all border border-slate-600/50 cursor-pointer"
            title="Ver detalhes"
          >
            <span>Detalhes</span>
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>
    </article>
  `;
}

/**
 * Product Details Modal viewer
 */
window.showProductDetails = function (productId) {
  const product = products.find((p) => p.id === productId);
  if (!product) return;

  const isBebida = product.tipo === "BEBIDA";
  const imageSource = product.imagemUrl && product.imagemUrl.trim().length > 0
    ? escapeHtml(product.imagemUrl)
    : getPlaceholderImage(product.nome, isBebida);

  const modalBackdrop = document.createElement("div");
  modalBackdrop.className =
    "fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 modal-backdrop";

  modalBackdrop.innerHTML = `
    <div class="relative w-full max-w-lg overflow-hidden rounded-2xl bg-slate-900 border border-slate-700 shadow-2xl animate-in fade-in zoom-in-95 duration-200">
      <button
        type="button"
        id="close-modal-btn"
        class="absolute top-4 right-4 z-10 p-2 rounded-full bg-slate-900/80 text-slate-400 hover:text-white hover:bg-slate-800 transition-colors border border-slate-700"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>

      <div class="relative aspect-video w-full overflow-hidden bg-slate-950">
        <img
          src="${imageSource}"
          alt="${escapeHtml(product.nome)}"
          onerror="window.__beergodImageFallback(this, ${isBebida})"
          class="w-full h-full object-cover"
        />
        <div class="absolute bottom-3 left-4">
          <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-slate-900/90 text-amber-400 border border-amber-500/40 backdrop-blur-md">
            ${escapeHtml(product.categoriaNome)}
          </span>
        </div>
      </div>

      <div class="p-6">
        <h2 class="text-2xl font-bold text-slate-100">${escapeHtml(product.nome)}</h2>

        ${
          isBebida
            ? `
          <div class="flex items-center gap-2 mt-3">
            ${
              product.marca
                ? `<span class="px-3 py-1 rounded-lg text-xs font-semibold bg-amber-500/15 text-amber-300 border border-amber-500/30">Marca: ${escapeHtml(product.marca)}</span>`
                : ""
            }
            ${
              product.tamanhoML
                ? `<span class="px-3 py-1 rounded-lg text-xs font-semibold bg-sky-500/15 text-sky-300 border border-sky-500/30">Volume: ${product.tamanhoML}ml</span>`
                : ""
            }
          </div>`
            : ""
        }

        <p class="mt-4 text-slate-300 leading-relaxed text-sm">
          ${escapeHtml(product.descricao) || "Nenhuma descrição detalhada informada para este produto."}
        </p>

        <div class="mt-6 pt-5 border-t border-slate-800 flex items-center justify-between">
          <div>
            <span class="text-xs uppercase text-slate-400 font-medium block">Preço</span>
            <span class="text-2xl font-black text-amber-400 font-serif-brand">
              ${formatBRL(product.preco)}
            </span>
          </div>

          <button
            type="button"
            id="modal-ok-btn"
            class="px-5 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold transition-all shadow-md amber-glow-sm"
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  `;

  document.body.appendChild(modalBackdrop);

  const closeModal = () => {
    if (modalBackdrop.parentElement) {
      modalBackdrop.parentElement.removeChild(modalBackdrop);
    }
  };

  modalBackdrop.querySelector("#close-modal-btn").addEventListener("click", closeModal);
  modalBackdrop.querySelector("#modal-ok-btn").addEventListener("click", closeModal);
  modalBackdrop.addEventListener("click", (e) => {
    if (e.target === modalBackdrop) closeModal();
  });
};

/**
 * Render Skeleton Loading Cards
 */
function renderSkeletons() {
  if (!productsGrid) return;

  const skeletonHtml = Array.from({ length: 6 })
    .map(
      () => `
      <div class="overflow-hidden rounded-2xl bg-slate-800/60 border border-slate-700/60 p-4 flex flex-col gap-3">
        <div class="w-full aspect-[4/3] rounded-xl skeleton"></div>
        <div class="h-5 w-3/4 rounded skeleton mt-2"></div>
        <div class="h-4 w-1/2 rounded skeleton"></div>
        <div class="h-10 w-full rounded skeleton mt-auto"></div>
      </div>
    `
    )
    .join("");

  productsGrid.innerHTML = skeletonHtml;
}

// Auto-run on DOM ready
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initMenu);
} else {
  initMenu();
}

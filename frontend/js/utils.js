/**
 * Utilities and Helpers for BeerGod Frontend
 */

const STORAGE_KEY = "beergod_admin";

/**
 * Format a number or string into Brazilian Real (BRL) currency: R$ 00,00
 */
export function formatBRL(value) {
  if (value === null || value === undefined || isNaN(Number(value))) {
    return "R$ 0,00";
  }
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(Number(value));
}

/**
 * Escape HTML to prevent XSS attacks when rendering dynamic content
 */
export function escapeHtml(str) {
  if (str === null || str === undefined) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

/**
 * Elegant SVG placeholder generator for products with missing or broken images
 */
export function getPlaceholderImage(nome = "BeerGod", isBebida = false) {
  const icon = isBebida
    ? `<path d="M5 2h14v3l-5 6.5V19h4v2H6v-2h4v-7.5L5 5V2zm2 2v1.5l4 5.2 4-5.2V4H7z" fill="#f59e0b"/>`
    : `<path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" fill="#f59e0b"/>`;

  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
    <defs>
      <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#0f172a" />
        <stop offset="100%" stop-color="#1e293b" />
      </linearGradient>
    </defs>
    <rect width="100%" height="100%" fill="url(#bg)" />
    <g transform="translate(176, 110) scale(2)">
      ${icon}
    </g>
    <text x="50%" y="200" fill="#94a3b8" font-family="system-ui, sans-serif" font-size="14" font-weight="600" text-anchor="middle" letter-spacing="1">
      BEERGOD CRAFT
    </text>
  </svg>`;

  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;
}

/**
 * Handle image load errors gracefully by replacing source with fallback
 */
export function handleImageError(imgElement, isBebida = false) {
  imgElement.onerror = null;
  imgElement.src = getPlaceholderImage("BeerGod", isBebida);
}

/**
 * LocalStorage session helpers for Administrator
 */
export function getStoredAdmin() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function setStoredAdmin(admin) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(admin));
}

export function removeStoredAdmin() {
  localStorage.removeItem(STORAGE_KEY);
}

export function isAuthenticated() {
  const admin = getStoredAdmin();
  return Boolean(admin && admin.id && admin.email && admin.token);
}

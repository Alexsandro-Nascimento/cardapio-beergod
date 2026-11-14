/**
 * Toast Notification System for BeerGod
 * Displays stackable notifications in the top-right corner.
 */

let toastContainer = null;

function getOrCreateToastContainer() {
  if (!toastContainer || !document.body.contains(toastContainer)) {
    toastContainer = document.getElementById("toast-container");
    if (!toastContainer) {
      toastContainer = document.createElement("div");
      toastContainer.id = "toast-container";
      toastContainer.className =
        "fixed top-4 right-4 z-50 flex flex-col gap-3 w-full max-w-sm pointer-events-none px-3 sm:px-0";
      document.body.appendChild(toastContainer);
    }
  }
  return toastContainer;
}

/**
 * Show a toast notification
 * @param {string} message - Message to display
 * @param {"success" | "error" | "warning" | "info"} type - Type of toast
 * @param {number} duration - Auto dismiss time in ms (default 4000)
 */
export function showToast(message, type = "info", duration = 4000) {
  const container = getOrCreateToastContainer();

  const typeConfig = {
    success: {
      bgColor: "bg-slate-900/95 border-emerald-500/60 text-emerald-300",
      iconColor: "text-emerald-400",
      accentBar: "bg-emerald-500",
      iconSvg: `
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>`,
      title: "Sucesso",
    },
    error: {
      bgColor: "bg-slate-900/95 border-rose-500/60 text-rose-300",
      iconColor: "text-rose-400",
      accentBar: "bg-rose-500",
      iconSvg: `
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>`,
      title: "Erro",
    },
    warning: {
      bgColor: "bg-slate-900/95 border-amber-500/60 text-amber-300",
      iconColor: "text-amber-400",
      accentBar: "bg-amber-500",
      iconSvg: `
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>`,
      title: "Atenção",
    },
    info: {
      bgColor: "bg-slate-900/95 border-sky-500/60 text-sky-300",
      iconColor: "text-sky-400",
      accentBar: "bg-sky-500",
      iconSvg: `
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>`,
      title: "Informação",
    },
  };

  const config = typeConfig[type] || typeConfig.info;

  const toast = document.createElement("div");
  toast.className = `pointer-events-auto transform transition-all duration-300 ease-out translate-x-full opacity-0 relative overflow-hidden rounded-xl border p-4 shadow-2xl backdrop-blur-md ${config.bgColor}`;

  toast.innerHTML = `
    <div class="flex items-start gap-3">
      <div class="${config.iconColor} pt-0.5">${config.iconSvg}</div>
      <div class="flex-1 min-w-0 pr-2">
        <p class="text-xs font-semibold uppercase tracking-wider ${config.iconColor}">${config.title}</p>
        <p class="text-sm font-medium text-slate-200 mt-0.5 break-words leading-snug">${escapeHtml(message)}</p>
      </div>
      <button type="button" class="text-slate-400 hover:text-slate-100 transition-colors p-1 rounded-lg hover:bg-slate-800/60 focus:outline-none" aria-label="Fechar">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
    <div class="toast-progress absolute bottom-0 left-0 h-1 ${config.accentBar} transition-all" style="width: 100%;"></div>
  `;

  const closeBtn = toast.querySelector("button");
  const progressBar = toast.querySelector(".toast-progress");

  let isDismissed = false;

  function dismiss() {
    if (isDismissed) return;
    isDismissed = true;
    toast.classList.remove("translate-x-0", "opacity-100");
    toast.classList.add("translate-x-full", "opacity-0");
    setTimeout(() => {
      if (toast.parentElement) {
        toast.parentElement.removeChild(toast);
      }
    }, 300);
  }

  closeBtn.addEventListener("click", dismiss);

  container.appendChild(toast);

  // Trigger enter animation
  requestAnimationFrame(() => {
    toast.classList.remove("translate-x-full", "opacity-0");
    toast.classList.add("translate-x-0", "opacity-100");
  });

  // Animated progress bar and auto dismiss
  const startTime = Date.now();
  const interval = 20;

  const timer = setInterval(() => {
    if (isDismissed) {
      clearInterval(timer);
      return;
    }
    const elapsed = Date.now() - startTime;
    const remaining = Math.max(0, 100 - (elapsed / duration) * 100);
    if (progressBar) {
      progressBar.style.width = `${remaining}%`;
    }
    if (elapsed >= duration) {
      clearInterval(timer);
      dismiss();
    }
  }, interval);
}

function escapeHtml(text) {
  if (text === null || text === undefined) return "";
  return String(text)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

export const toast = {
  success: (msg, duration) => showToast(msg, "success", duration),
  error: (msg, duration) => showToast(msg, "error", duration),
  warning: (msg, duration) => showToast(msg, "warning", duration),
  info: (msg, duration) => showToast(msg, "info", duration),
};

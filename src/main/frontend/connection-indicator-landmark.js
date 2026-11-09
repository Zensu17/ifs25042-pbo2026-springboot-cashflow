(() => {
  const applyLandmark = () => {
    const indicator = document.querySelector('vaadin-connection-indicator');
    if (!indicator) {
      return false;
    }

    indicator.setAttribute('role', 'region');
    indicator.setAttribute('aria-label', 'Status koneksi aplikasi');
    return true;
  };

  const observeIndicator = () => {
    if (applyLandmark()) {
      return;
    }

    const observer = new MutationObserver(() => {
      if (applyLandmark()) {
        observer.disconnect();
      }
    });
    observer.observe(document.documentElement, { childList: true, subtree: true });
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', observeIndicator, { once: true });
  } else {
    observeIndicator();
  }
})();
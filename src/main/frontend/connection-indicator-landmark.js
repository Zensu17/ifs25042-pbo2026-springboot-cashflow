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

	if (applyLandmark()) {
		return;
	}

	const observer = new MutationObserver(() => {
		if (applyLandmark()) {
			observer.disconnect();
		}
	});

	observer.observe(document.documentElement, { childList: true, subtree: true });
})();

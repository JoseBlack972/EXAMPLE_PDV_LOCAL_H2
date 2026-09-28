const CACHE_NAME = 'pdv-cache-v1';

self.addEventListener('install', (event) => {
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
    // Rede prioritária
    event.respondWith(fetch(event.request).catch(() => caches.match(event.request)));
});

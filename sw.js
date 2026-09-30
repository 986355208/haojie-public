const CACHE='haojie-life-v4';
const ASSETS=['./','./index.html','./style.css','./main.js','./core.js','./scene.js','./icon.svg','./manifest.webmanifest','./vendor/three.module.js','./vendor/OBJLoader.js','./assets/hood-rabbit.obj','./assets/hood-rabbit.jpg','./assets/pet.obj','./assets/pet.jpg'];
self.addEventListener('install',e=>{e.waitUntil(caches.open(CACHE).then(c=>c.addAll(ASSETS)).then(()=>self.skipWaiting()));});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(k=>k.startsWith('haojie-life-')&&k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim()));});
self.addEventListener('fetch',e=>{if(e.request.method!=='GET'||new URL(e.request.url).origin!==location.origin)return;e.respondWith(fetch(e.request).then(r=>{if(r.ok){const clone=r.clone();caches.open(CACHE).then(c=>c.put(e.request,clone));}return r;}).catch(()=>caches.match(e.request).then(r=>r|| (e.request.mode==='navigate'?caches.match('./index.html'):Response.error()))));});


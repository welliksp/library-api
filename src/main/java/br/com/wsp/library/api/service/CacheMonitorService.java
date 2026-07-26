package br.com.wsp.library.api.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheMonitorService {

    private final CacheManager cacheManager;

    public void verificarCache(String id) {
        Cache cache = cacheManager.getCache("livro");
        if (cache != null) {
            Cache.ValueWrapper value = cache.get(id);
            if (value != null) {
                log.info("Cache HIT - ID: {}", id);
                log.info("Dados em cache: {}", value.get());
            } else {
                log.info("Cache MISS - ID: {}", id);
            }
        }
    }

    public void limparCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cache limpo: {}", cacheName);
            }
        });
    }
}
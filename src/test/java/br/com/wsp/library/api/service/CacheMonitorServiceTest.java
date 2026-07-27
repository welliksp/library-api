package br.com.wsp.library.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheMonitorServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CacheMonitorService service;

    @Nested
    @DisplayName("verificarCache")
    class VerificarCache {

        @Test
        @DisplayName("deve logar cache HIT quando valor encontrado")
        void deveLogarCacheHit() {
            Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
            when(cacheManager.getCache("livro")).thenReturn(cache);
            when(cache.get("123")).thenReturn(valueWrapper);
            when(valueWrapper.get()).thenReturn("dadoEmCache");

            service.verificarCache("123");

            verify(cache).get("123");
            verify(valueWrapper).get();
        }

        @Test
        @DisplayName("deve logar cache MISS quando valor não encontrado")
        void deveLogarCacheMiss() {
            when(cacheManager.getCache("livro")).thenReturn(cache);
            when(cache.get("123")).thenReturn(null);

            service.verificarCache("123");

            verify(cache).get("123");
        }

        @Test
        @DisplayName("não deve interagir com cache quando getCache retornar null")
        void naoDeveInteragirQuandoCacheNull() {
            when(cacheManager.getCache("livro")).thenReturn(null);

            service.verificarCache("123");

            verify(cache, never()).get(any());
        }
    }

    @Nested
    @DisplayName("limparCache")
    class LimparCache {

        @Test
        @DisplayName("deve limpar todos os caches")
        void deveLimparTodosOsCaches() {
            when(cacheManager.getCacheNames()).thenReturn(List.of("livro", "livros"));
            when(cacheManager.getCache("livro")).thenReturn(cache);
            when(cacheManager.getCache("livros")).thenReturn(cache);

            service.limparCache();

            verify(cache, times(2)).clear();
        }

        @Test
        @DisplayName("não deve chamar clear quando getCache retornar null")
        void naoDeveChamarClearQuandoCacheNull() {
            when(cacheManager.getCacheNames()).thenReturn(List.of("livro"));
            when(cacheManager.getCache("livro")).thenReturn(null);

            service.limparCache();

            verify(cache, never()).clear();
        }

        @Test
        @DisplayName("não deve fazer nada quando não houver caches")
        void naoDeveFazerNadaQuandoSemCaches() {
            when(cacheManager.getCacheNames()).thenReturn(List.of());

            service.limparCache();

            verify(cacheManager, never()).getCache(any());
        }
    }
}

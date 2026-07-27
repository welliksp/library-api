package br.com.wsp.library.api.exception;

import br.com.wsp.library.api.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler - Testes Unitários")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("deve retornar 422 com codigo e mensagem ao tratar NegocioException")
    void deveTratarNegocioException() {
        NegocioException ex = new NegocioException(NegocioException.LIVRO_NAO_ENCONTRADO, "Livro não encontrado.");

        ResponseEntity<ErrorResponseDTO> response = handler.handleNegocioException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(NegocioException.LIVRO_NAO_ENCONTRADO, response.getBody().codigo());
        assertEquals("Livro não encontrado.", response.getBody().mensagem());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("deve retornar 422 com causa ao tratar NegocioException com Throwable")
    void deveTratarNegocioExceptionComCausa() {
        NegocioException ex = new NegocioException(NegocioException.LIVRO_ISBN_DUPLICADO, "ISBN duplicado.", new RuntimeException("causa"));

        ResponseEntity<ErrorResponseDTO> response = handler.handleNegocioException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(NegocioException.LIVRO_ISBN_DUPLICADO, response.getBody().codigo());
        assertEquals("ISBN duplicado.", response.getBody().mensagem());
    }

    @Test
    @DisplayName("deve retornar 400 com mensagem dos campos ao tratar MethodArgumentNotValidException")
    void deveTratarMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("livroRequestDTO", "titulo", "Título é obrigatório"),
                new FieldError("livroRequestDTO", "isbn", "ISBN é obrigatório")
        ));

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DADOS_INVALIDOS", response.getBody().codigo());
        assertEquals("titulo: Título é obrigatório, isbn: ISBN é obrigatório", response.getBody().mensagem());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("deve retornar 400 com mensagem única ao tratar MethodArgumentNotValidException com um campo")
    void deveTratarMethodArgumentNotValidExceptionComUmCampo() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("livroRequestDTO", "autor", "Autor é obrigatório")
        ));

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("DADOS_INVALIDOS", response.getBody().codigo());
        assertEquals("autor: Autor é obrigatório", response.getBody().mensagem());
    }

    @Test
    @DisplayName("deve retornar 500 com mensagem genérica ao tratar Exception")
    void deveTratarException() {
        Exception ex = new Exception("erro inesperado");

        ResponseEntity<ErrorResponseDTO> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERRO_INTERNO", response.getBody().codigo());
        assertEquals("Ocorreu um erro inesperado.", response.getBody().mensagem());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("deve retornar 500 ao tratar RuntimeException")
    void deveTratarRuntimeException() {
        RuntimeException ex = new RuntimeException("null pointer");

        ResponseEntity<ErrorResponseDTO> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO_INTERNO", response.getBody().codigo());
    }
}

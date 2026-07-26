package br.com.wsp.library.api.service;

import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;
import br.com.wsp.library.api.dto.PageResponseDTO;
import br.com.wsp.library.api.entity.enums.Genero;

public interface ILivroService {

    LivroResponseDTO criarLivro(LivroRequestDTO request);

    LivroResponseDTO buscarPorId(String id);

    PageResponseDTO<LivroResponseDTO> listarLivros(
            Genero genero,
            Integer pagina,
            Integer tamanho);

    LivroResponseDTO atualizarLivro(String id, LivroRequestDTO request);
}

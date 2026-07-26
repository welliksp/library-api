package br.com.wsp.library.api.service;

import br.com.wsp.library.api.dto.LivroRequestDTO;
import br.com.wsp.library.api.dto.LivroResponseDTO;

public interface ILivroService {

     LivroResponseDTO criarLivro(LivroRequestDTO request);

     LivroResponseDTO buscarPorId(String id);
}

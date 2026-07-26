package br.com.wsp.library_api.service;

import br.com.wsp.library_api.dto.LivroRequestDTO;
import br.com.wsp.library_api.dto.LivroResponseDTO;

public interface ILivroService {

     LivroResponseDTO criarLivro(LivroRequestDTO request);
}

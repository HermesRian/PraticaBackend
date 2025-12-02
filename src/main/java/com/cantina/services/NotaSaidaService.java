package com.cantina.services;

import com.cantina.entities.NotaSaida;
import java.util.List;

public interface NotaSaidaService {
    NotaSaida salvar(NotaSaida notaSaida);
    List<NotaSaida> listarTodas();
    NotaSaida buscarPorId(Long id);
    NotaSaida buscarPorNumero(String numero);
    NotaSaida atualizar(NotaSaida notaSaida);
    void excluir(Long id);
    void atualizarStatus(Long id, String novoStatus);
    void cancelarNota(Long id);
}

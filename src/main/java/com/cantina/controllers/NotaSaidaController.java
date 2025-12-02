package com.cantina.controllers;

import com.cantina.entities.NotaSaida;
import com.cantina.services.NotaSaidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notas-saida")
@Tag(name = "Notas de Saída", description = "Gerenciamento de notas de saída (vendas)")
public class NotaSaidaController {

    @Autowired
    private NotaSaidaService notaSaidaService;

    @GetMapping
    @Operation(summary = "Listar todas as notas de saída", description = "Retorna uma lista com todas as notas de saída ativas")
    @ApiResponse(responseCode = "200", description = "Lista de notas de saída retornada com sucesso")
    public List<NotaSaida> listarTodas() {
        return notaSaidaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota de saída por ID", description = "Retorna uma nota de saída específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de saída encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota de saída não encontrada")
    })
    public NotaSaida buscarPorId(@Parameter(description = "ID da nota de saída") @PathVariable Long id) {
        return notaSaidaService.buscarPorId(id);
    }

    @GetMapping("/numero/{numero}")
    @Operation(summary = "Buscar nota de saída por número", description = "Retorna uma nota de saída específica pelo seu número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de saída encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota de saída não encontrada")
    })
    public NotaSaida buscarPorNumero(@Parameter(description = "Número da nota de saída") @PathVariable String numero) {
        return notaSaidaService.buscarPorNumero(numero);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova nota de saída", description = "Cadastra uma nova nota de saída no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de saída cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody NotaSaida notaSaida) {
        try {
            NotaSaida novaNotaSaida = notaSaidaService.salvar(notaSaida);
            return ResponseEntity.ok(novaNotaSaida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota de saída", description = "Atualiza os dados de uma nota de saída existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de saída atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Nota de saída não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da nota de saída") @PathVariable Long id,
                                       @RequestBody NotaSaida notaSaida) {
        try {
            NotaSaida notaExistente = notaSaidaService.buscarPorId(id);
            if (notaExistente != null) {
                notaSaida.setId(id);
                NotaSaida notaAtualizada = notaSaidaService.atualizar(notaSaida);
                return ResponseEntity.ok(notaAtualizada);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Nota de saída não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da nota de saída", description = "Atualiza apenas o status de uma nota de saída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota de saída não encontrada")
    })
    public ResponseEntity<?> atualizarStatus(@Parameter(description = "ID da nota de saída") @PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        try {
            String novoStatus = body.get("status");
            notaSaidaService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(Map.of("mensagem", "Status atualizado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir nota de saída", description = "Desativa uma nota de saída (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de saída excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota de saída não encontrada")
    })
    public void excluir(@Parameter(description = "ID da nota de saída") @PathVariable Long id) {
        notaSaidaService.excluir(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar nota de saída", description = "Cancela uma nota de saída e reverte a baixa de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nota não pode ser cancelada (status inválido)"),
            @ApiResponse(responseCode = "404", description = "Nota de saída não encontrada")
    })
    public ResponseEntity<?> cancelarNota(@Parameter(description = "ID da nota de saída") @PathVariable Long id) {
        try {
            notaSaidaService.cancelarNota(id);
            return ResponseEntity.ok(Map.of("mensagem", "Nota cancelada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }
}

package com.cantina.controllers;

import com.cantina.entities.NotaEntrada;
import com.cantina.services.NotaEntradaService;
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
@RequestMapping("/notas-entrada")
@Tag(name = "Notas de Entrada", description = "Gerenciamento de notas de entrada (compras)")
public class NotaEntradaController {

    @Autowired
    private NotaEntradaService notaEntradaService;

    @GetMapping
    @Operation(summary = "Listar todas as notas de entrada", description = "Retorna uma lista com todas as notas de entrada ativas")
    @ApiResponse(responseCode = "200", description = "Lista de notas de entrada retornada com sucesso")
    public List<NotaEntrada> listarTodas() {
        return notaEntradaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota de entrada por ID", description = "Retorna uma nota de entrada específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de entrada encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota de entrada não encontrada")
    })
    public NotaEntrada buscarPorId(@Parameter(description = "ID da nota de entrada") @PathVariable Long id) {
        return notaEntradaService.buscarPorId(id);
    }

    @GetMapping("/numero/{numero}")
    @Operation(summary = "Buscar nota de entrada por número", description = "Retorna uma nota de entrada específica pelo seu número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de entrada encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota de entrada não encontrada")
    })
    public NotaEntrada buscarPorNumero(@Parameter(description = "Número da nota de entrada") @PathVariable String numero) {
        return notaEntradaService.buscarPorNumero(numero);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova nota de entrada", description = "Cadastra uma nova nota de entrada no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de entrada cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody NotaEntrada notaEntrada) {
        try {
            NotaEntrada novaNotaEntrada = notaEntradaService.salvar(notaEntrada);
            return ResponseEntity.ok(novaNotaEntrada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota de entrada", description = "Atualiza os dados de uma nota de entrada existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de entrada atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Nota de entrada não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da nota de entrada") @PathVariable Long id,
                                       @RequestBody NotaEntrada notaEntrada) {
        try {
            NotaEntrada notaExistente = notaEntradaService.buscarPorId(id);
            if (notaExistente != null) {
                notaEntrada.setId(id);
                NotaEntrada notaAtualizada = notaEntradaService.atualizar(notaEntrada);
                return ResponseEntity.ok(notaAtualizada);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Nota de entrada não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da nota de entrada", description = "Atualiza apenas o status de uma nota de entrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota de entrada não encontrada")
    })
    public ResponseEntity<?> atualizarStatus(@Parameter(description = "ID da nota de entrada") @PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        try {
            String novoStatus = body.get("status");
            notaEntradaService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(Map.of("mensagem", "Status atualizado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir nota de entrada", description = "Desativa uma nota de entrada (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota de entrada excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota de entrada não encontrada")
    })
    public void excluir(@Parameter(description = "ID da nota de entrada") @PathVariable Long id) {
        notaEntradaService.excluir(id);
    }
}

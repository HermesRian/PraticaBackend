package com.cantina.controllers;

import com.cantina.entities.UnidadeMedida;
import com.cantina.services.UnidadeMedidaService;
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
@RequestMapping("/unidades-medida")
@Tag(name = "Unidades de Medida", description = "Gerenciamento de unidades de medida")
public class UnidadeMedidaController {

    @Autowired
    private UnidadeMedidaService unidadeMedidaService;

    @GetMapping
    @Operation(summary = "Listar todas as unidades de medida", description = "Retorna uma lista com todas as unidades de medida cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de unidades de medida retornada com sucesso")
    public List<UnidadeMedida> listarTodos() {
        return unidadeMedidaService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade de medida por ID", description = "Retorna uma unidade de medida específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unidade de medida encontrada"),
        @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada")
    })
    public UnidadeMedida buscarPorId(@Parameter(description = "ID da unidade de medida") @PathVariable Long id) {
        return unidadeMedidaService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova unidade de medida", description = "Cadastra uma nova unidade de medida no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unidade de medida cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody UnidadeMedida unidadeMedida) {
        try {
            UnidadeMedida novaUnidadeMedida = unidadeMedidaService.salvar(unidadeMedida);
            return ResponseEntity.ok(novaUnidadeMedida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir unidade de medida", description = "Remove uma unidade de medida do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unidade de medida excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada")
    })
    public void excluir(@Parameter(description = "ID da unidade de medida") @PathVariable Long id) {
        unidadeMedidaService.excluir(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar unidade de medida", description = "Atualiza os dados de uma unidade de medida existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unidade de medida atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Unidade de medida não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da unidade de medida") @PathVariable Long id, @RequestBody UnidadeMedida unidadeMedida) {
        try {
            UnidadeMedida unidadeMedidaExistente = unidadeMedidaService.buscarPorId(id);
            if (unidadeMedidaExistente != null) {
                unidadeMedida.setId(id);
                UnidadeMedida unidadeMedidaAtualizada = unidadeMedidaService.atualizar(id, unidadeMedida);
                return ResponseEntity.ok(unidadeMedidaAtualizada);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Unidade de medida não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }
}
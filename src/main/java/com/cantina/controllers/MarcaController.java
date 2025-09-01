package com.cantina.controllers;

import com.cantina.entities.Marca;
import com.cantina.services.MarcaService;
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
@RequestMapping("/marcas")
@Tag(name = "Marcas", description = "Gerenciamento de marcas de produtos")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    @Operation(summary = "Listar todas as marcas", description = "Retorna uma lista com todas as marcas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de marcas retornada com sucesso")
    public List<Marca> listarTodos() {
        return marcaService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marca por ID", description = "Retorna uma marca específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Marca encontrada"),
        @ApiResponse(responseCode = "404", description = "Marca não encontrada")
    })
    public Marca buscarPorId(@Parameter(description = "ID da marca") @PathVariable Long id) {
        return marcaService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova marca", description = "Cadastra uma nova marca no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Marca cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody Marca marca) {
        try {
            Marca novaMarca = marcaService.salvar(marca);
            return ResponseEntity.ok(novaMarca);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir marca", description = "Remove uma marca do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Marca excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Marca não encontrada")
    })
    public void excluir(@Parameter(description = "ID da marca") @PathVariable Long id) {
        marcaService.excluir(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar marca", description = "Atualiza os dados de uma marca existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Marca atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Marca não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da marca") @PathVariable Long id, @RequestBody Marca marca) {
        try {
            Marca marcaExistente = marcaService.buscarPorId(id);
            if (marcaExistente != null) {
                marca.setId(id);
                Marca marcaAtualizada = marcaService.atualizar(id, marca);
                return ResponseEntity.ok(marcaAtualizada);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Marca não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }
}
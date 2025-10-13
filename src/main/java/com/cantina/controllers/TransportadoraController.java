package com.cantina.controllers;

import com.cantina.entities.Transportadora;
import com.cantina.services.TransportadoraService;
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
@RequestMapping("/transportadoras")
@Tag(name = "Transportadoras", description = "Gerenciamento de transportadoras")
public class TransportadoraController {

    @Autowired
    private TransportadoraService transportadoraService;

    @GetMapping
    @Operation(summary = "Listar todas as transportadoras", description = "Retorna uma lista com todas as transportadoras cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de transportadoras retornada com sucesso")
    public List<Transportadora> listarTodas() {
        return transportadoraService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transportadora por ID", description = "Retorna uma transportadora específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transportadora encontrada"),
            @ApiResponse(responseCode = "404", description = "Transportadora não encontrada")
    })
    public Transportadora buscarPorId(@Parameter(description = "ID da transportadora") @PathVariable Long id) {
        return transportadoraService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova transportadora", description = "Cadastra uma nova transportadora no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transportadora cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/CNPJ duplicado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody Transportadora transportadora) {
        try {
            transportadoraService.salvar(transportadora);
            return ResponseEntity.ok(transportadora);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transportadora", description = "Remove uma transportadora do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transportadora excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Transportadora não encontrada")
    })
    public void excluir(@Parameter(description = "ID da transportadora") @PathVariable Long id) {
        transportadoraService.excluir(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transportadora", description = "Atualiza os dados de uma transportadora existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transportadora atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/CNPJ duplicado"),
            @ApiResponse(responseCode = "404", description = "Transportadora não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da transportadora") @PathVariable Long id, @RequestBody Transportadora transportadora) {
        try {
            Transportadora transportadoraExistente = transportadoraService.buscarPorId(id);
            if (transportadoraExistente != null) {
                transportadora.setId(id);
                transportadoraService.atualizar(transportadora);
                return ResponseEntity.ok(transportadora);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Transportadora não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }
}
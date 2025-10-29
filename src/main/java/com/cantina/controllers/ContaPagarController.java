package com.cantina.controllers;

import com.cantina.entities.ContaPagar;
import com.cantina.services.ContaPagarService;
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
@RequestMapping("/contas-pagar")
@Tag(name = "Contas a Pagar", description = "Gerenciamento de contas a pagar")
public class ContaPagarController {

    @Autowired
    private ContaPagarService contaPagarService;

    @GetMapping
    @Operation(summary = "Listar todas as contas a pagar", description = "Retorna uma lista com todas as contas a pagar ativas")
    @ApiResponse(responseCode = "200", description = "Lista de contas a pagar retornada com sucesso")
    public List<ContaPagar> listarTodas() {
        return contaPagarService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a pagar por ID", description = "Retorna uma conta a pagar específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a pagar encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta a pagar não encontrada")
    })
    public ContaPagar buscarPorId(@Parameter(description = "ID da conta a pagar") @PathVariable Long id) {
        return contaPagarService.buscarPorId(id);
    }

    @GetMapping("/nota-entrada/{notaEntradaId}")
    @Operation(summary = "Buscar contas a pagar por Nota de Entrada", description = "Retorna todas as contas a pagar de uma nota de entrada")
    @ApiResponse(responseCode = "200", description = "Lista de contas a pagar retornada com sucesso")
    public List<ContaPagar> buscarPorNotaEntradaId(
            @Parameter(description = "ID da nota de entrada") @PathVariable Long notaEntradaId) {
        return contaPagarService.buscarPorNotaEntradaId(notaEntradaId);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova conta a pagar", description = "Cadastra uma nova conta a pagar no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a pagar cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody ContaPagar contaPagar) {
        try {
            ContaPagar novaContaPagar = contaPagarService.salvar(contaPagar);
            return ResponseEntity.ok(novaContaPagar);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a pagar", description = "Atualiza os dados de uma conta a pagar existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a pagar atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta a pagar não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da conta a pagar") @PathVariable Long id,
                                       @RequestBody ContaPagar contaPagar) {
        try {
            ContaPagar contaExistente = contaPagarService.buscarPorId(id);
            if (contaExistente != null) {
                contaPagar.setId(id);
                ContaPagar contaAtualizada = contaPagarService.atualizar(contaPagar);
                return ResponseEntity.ok(contaAtualizada);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Conta a pagar não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/pagar")
    @Operation(summary = "Marcar conta como paga", description = "Marca uma conta a pagar como PAGA e atualiza o estoque se for a primeira parcela")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta marcada como paga com sucesso"),
            @ApiResponse(responseCode = "400", description = "Conta já está paga ou cancelada"),
            @ApiResponse(responseCode = "404", description = "Conta a pagar não encontrada")
    })
    public ResponseEntity<?> marcarComoPaga(@Parameter(description = "ID da conta a pagar") @PathVariable Long id) {
        try {
            contaPagarService.marcarComoPaga(id);
            return ResponseEntity.ok(Map.of("mensagem", "Conta marcada como paga com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar conta a pagar", description = "Cancela uma conta a pagar. Se a nota estiver CONFIRMADA, cancela todas as parcelas e a nota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível cancelar uma conta já paga"),
            @ApiResponse(responseCode = "404", description = "Conta a pagar não encontrada")
    })
    public ResponseEntity<?> cancelar(@Parameter(description = "ID da conta a pagar") @PathVariable Long id) {
        try {
            contaPagarService.cancelar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Conta cancelada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta a pagar", description = "Desativa uma conta a pagar (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a pagar excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta a pagar não encontrada")
    })
    public void excluir(@Parameter(description = "ID da conta a pagar") @PathVariable Long id) {
        contaPagarService.excluir(id);
    }
}
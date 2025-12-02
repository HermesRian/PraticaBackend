package com.cantina.controllers;

import com.cantina.entities.ContaReceber;
import com.cantina.services.ContaReceberService;
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
@RequestMapping("/contas-receber")
@Tag(name = "Contas a Receber", description = "Gerenciamento de contas a receber")
public class ContaReceberController {

    @Autowired
    private ContaReceberService contaReceberService;

    @GetMapping
    @Operation(summary = "Listar todas as contas a receber", description = "Retorna uma lista com todas as contas a receber ativas")
    @ApiResponse(responseCode = "200", description = "Lista de contas a receber retornada com sucesso")
    public List<ContaReceber> listarTodas() {
        return contaReceberService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a receber por ID", description = "Retorna uma conta a receber específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a receber encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta a receber não encontrada")
    })
    public ContaReceber buscarPorId(@Parameter(description = "ID da conta a receber") @PathVariable Long id) {
        return contaReceberService.buscarPorId(id);
    }

    @GetMapping("/nota-saida/{notaSaidaId}")
    @Operation(summary = "Buscar contas a receber por Nota de Saída", description = "Retorna todas as contas a receber de uma nota de saída")
    @ApiResponse(responseCode = "200", description = "Lista de contas a receber retornada com sucesso")
    public List<ContaReceber> buscarPorNotaSaidaId(
            @Parameter(description = "ID da nota de saída") @PathVariable Long notaSaidaId) {
        return contaReceberService.buscarPorNotaSaidaId(notaSaidaId);
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova conta a receber", description = "Cadastra uma nova conta a receber no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a receber cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody ContaReceber contaReceber) {
        try {
            ContaReceber novaContaReceber = contaReceberService.salvar(contaReceber);
            return ResponseEntity.ok(novaContaReceber);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a receber", description = "Atualiza os dados de uma conta a receber existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a receber atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta a receber não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da conta a receber") @PathVariable Long id,
                                       @RequestBody ContaReceber contaReceber) {
        try {
            ContaReceber contaExistente = contaReceberService.buscarPorId(id);
            if (contaExistente != null) {
                contaReceber.setId(id);
                ContaReceber contaAtualizada = contaReceberService.atualizar(contaReceber);
                return ResponseEntity.ok(contaAtualizada);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Conta a receber não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/pagar")
    @Operation(summary = "Marcar conta como paga", description = "Marca uma conta a receber como PAGA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta marcada como paga com sucesso"),
            @ApiResponse(responseCode = "400", description = "Conta já está paga ou cancelada"),
            @ApiResponse(responseCode = "404", description = "Conta a receber não encontrada")
    })
    public ResponseEntity<?> marcarComoPaga(@Parameter(description = "ID da conta a receber") @PathVariable Long id) {
        try {
            contaReceberService.marcarComoPaga(id);
            return ResponseEntity.ok(Map.of("mensagem", "Conta marcada como paga com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar conta a receber", description = "Cancela uma conta a receber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível cancelar uma conta já paga"),
            @ApiResponse(responseCode = "404", description = "Conta a receber não encontrada")
    })
    public ResponseEntity<?> cancelar(@Parameter(description = "ID da conta a receber") @PathVariable Long id) {
        try {
            contaReceberService.cancelar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Conta cancelada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta a receber", description = "Desativa uma conta a receber (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta a receber excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta a receber não encontrada")
    })
    public void excluir(@Parameter(description = "ID da conta a receber") @PathVariable Long id) {
        contaReceberService.excluir(id);
    }
}

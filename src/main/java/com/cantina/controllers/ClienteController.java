package com.cantina.controllers;

import com.cantina.entities.Cliente;
import com.cantina.exceptions.DuplicateCnpjCpfException;
import com.cantina.services.ClienteService;
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
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public List<Cliente> listarTodos() {

        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Cliente buscarPorId(@Parameter(description = "ID do cliente") @PathVariable Long id) {

        return clienteService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo cliente", description = "Cadastra um novo cliente no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/CNPJ duplicado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> salvar(@RequestBody Cliente cliente) {
        try {
            Cliente novoCliente = clienteService.salvar(cliente);
            return ResponseEntity.ok(novoCliente);
        } catch (DuplicateCnpjCpfException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public void excluir(@Parameter(description = "ID do cliente") @PathVariable Long id) {

        clienteService.excluir(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/CNPJ duplicado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizar(@Parameter(description = "ID do cliente") @PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Cliente clienteExistente = clienteService.buscarPorId(id);
            if (clienteExistente != null) {
                cliente.setId(id);
                Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
                return ResponseEntity.ok(clienteAtualizado);
            } else {
                return ResponseEntity.status(404).body(Map.of("erro", "Cliente não encontrado."));
            }
        } catch (DuplicateCnpjCpfException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro inesperado: " + e.getMessage()));
        }
    }
}
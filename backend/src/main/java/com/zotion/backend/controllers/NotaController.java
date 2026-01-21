package com.zotion.backend.controllers;

import com.zotion.backend.models.Nota;
import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.UsuarioRepository;
import com.zotion.backend.services.NotaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;
    private final UsuarioRepository usuarioRepository;

    public NotaController(NotaService notaService, UsuarioRepository usuarioRepository) {
        this.notaService = notaService;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario obterUsuarioLogado(JwtAuthenticationToken token) {
        return usuarioRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<Nota>> listar(JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        return ResponseEntity.ok(notaService.listarPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<Nota> criar(@RequestBody @Valid Nota nota, JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        return ResponseEntity.ok(notaService.salvar(nota, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id, JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        notaService.excluir(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Nota> editar(@PathVariable UUID id, @RequestBody @Valid Nota nota, JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        return ResponseEntity.ok(notaService.editar(id, nota, usuario));
    }
}
package com.zotion.backend.controllers;

import com.zotion.backend.models.Lembrete;
import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.UsuarioRepository;
import com.zotion.backend.services.LembreteService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lembretes")
public class LembreteController {

    private final LembreteService lembreteService;
    private final UsuarioRepository usuarioRepository;

    public LembreteController(LembreteService lembreteService, UsuarioRepository usuarioRepository) {
        this.lembreteService = lembreteService;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario obterUsuarioLogado(JwtAuthenticationToken token) {
        return usuarioRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<Lembrete>> listar(JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        return ResponseEntity.ok(lembreteService.listarPorUsuario(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lembrete> editar(@PathVariable UUID id, @RequestBody Lembrete dadosAtualizados, JwtAuthenticationToken token) {
        Usuario utilizador = usuarioRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        Lembrete lembrete = lembreteService.editar(id, dadosAtualizados, utilizador);
        return ResponseEntity.ok(lembrete);
    }

    @PostMapping
    public ResponseEntity<Lembrete> criar(@RequestBody @Valid Lembrete lembrete, JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        return ResponseEntity.ok(lembreteService.salvar(lembrete, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id, JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        lembreteService.excluir(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<Lembrete> alternarConclusao(@PathVariable UUID id, JwtAuthenticationToken token) {
        var usuario = obterUsuarioLogado(token);
        Lembrete lembrete = lembreteService.buscarPorId(id, usuario);
        lembrete.setRealizada(!lembrete.isRealizada());
        return ResponseEntity.ok(lembreteService.salvar(lembrete, usuario));
    }
}
package com.zotion.backend.controllers;

import com.zotion.backend.models.Lembrete;
import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.UsuarioRepository;
import com.zotion.backend.services.GeminiService;
import com.zotion.backend.services.LembreteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ia")
public class IAController {

    private final GeminiService geminiService;
    private final LembreteService lembreteService;
    private final UsuarioRepository usuarioRepository;

    public IAController(GeminiService geminiService, LembreteService lembreteService, UsuarioRepository usuarioRepository) {
        this.geminiService = geminiService;
        this.lembreteService = lembreteService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/processar-pdf")
    public ResponseEntity<List<Lembrete>> processarPdf(
            @RequestParam("arquivo") MultipartFile arquivo,
            JwtAuthenticationToken token) throws IOException {

        List<Lembrete> lembretesExtraidos = geminiService.extrairLembretesDeArquivo(arquivo.getBytes());

        Usuario usuario = usuarioRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        lembretesExtraidos.forEach(lembrete -> lembreteService.salvar(lembrete, usuario));

        return ResponseEntity.ok(lembretesExtraidos);
    }
}
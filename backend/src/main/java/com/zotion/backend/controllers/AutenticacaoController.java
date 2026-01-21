package com.zotion.backend.controllers;

import com.zotion.backend.dto.LoginRequisicao;
import com.zotion.backend.dto.LoginResposta;
import com.zotion.backend.models.Perfil;
import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.UsuarioRepository;
import com.zotion.backend.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder codificadorSenha;

    public AutenticacaoController(UsuarioService usuarioService,
                                  UsuarioRepository usuarioRepository,
                                  JwtEncoder jwtEncoder,
                                  BCryptPasswordEncoder codificadorSenha) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.jwtEncoder = jwtEncoder;
        this.codificadorSenha = codificadorSenha;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResposta> login(@RequestBody LoginRequisicao requisicao) {
        var usuario = usuarioRepository.findByEmail(requisicao.email());

        if (usuario.isEmpty() || !usuario.get().isSenhaCorreta(requisicao.senha(), codificadorSenha)) {
            throw new BadCredentialsException("E-mail ou senha inválidos!");
        }

        var agora = Instant.now();
        var expiracao = 300L; 

        var escopos = usuario.get().getPerfis()
                .stream()
                .map(Perfil::getNome)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("zotion-backend")
                .subject(usuario.get().getId().toString())
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expiracao))
                .claim("scope", escopos)
                .build();

        var tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResposta(tokenValue, expiracao));
    }
}
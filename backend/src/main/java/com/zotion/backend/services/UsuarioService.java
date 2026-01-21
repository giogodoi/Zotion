package com.zotion.backend.services;

import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.PerfilRepository;
import com.zotion.backend.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final BCryptPasswordEncoder codificadorSenha;

    //Usei o construtor para a injeção das dependencias
    //Poderiamos fazer essa injeção de algumas maneiras como por atributo, por função, ou por construtor.
    //Mas o construtor é o mais recomendado atualmente e além disso já possui implicito o @Autowired.
    //Usarei esse padrão em todos os demais serviços também, para manter a coerência.

    public UsuarioService(UsuarioRepository usuarioRepository, 
                          PerfilRepository perfilRepository, 
                          BCryptPasswordEncoder codificadorSenha) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.codificadorSenha = codificadorSenha;
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        });

        usuario.setSenha(codificadorSenha.encode(usuario.getSenha()));

        //Atrubuição do Perfil ao Usuário
        perfilRepository.findByNome("ROLE_USER").ifPresent(perfil -> {
            usuario.setPerfis(java.util.Set.of(perfil));
        });

        return usuarioRepository.save(usuario);
    }
}
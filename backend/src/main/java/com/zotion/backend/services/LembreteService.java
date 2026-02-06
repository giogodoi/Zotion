package com.zotion.backend.services;

import com.zotion.backend.models.Lembrete;
import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.LembreteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class LembreteService {

    private final LembreteRepository lembreteRepository;

    //Fiz o mesmo esquema que citei lá no UsuarioService para injeção de dependências
    //Usarei esse padrão em todos os demais serviços também, para manter a coerência.

    public LembreteService(LembreteRepository lembreteRepository) {
        this.lembreteRepository = lembreteRepository;
    }

    public List<Lembrete> listarPorUsuario(Usuario usuario) {
        Sort ordenacao = Sort.by("prioridade").descending()
                             .and(Sort.by("dataHora").ascending());
        
        //Achei coerente retornar primeiro as mais importantes e depois as mais proximas.
        
        return lembreteRepository.findAllByUsuario(usuario, ordenacao);
    }

    // De acordo com a documentação do Spring, o Transactional deve ser usado em métodos que modificam o banco de dados.
    // Portanto, adicionei @Transactional nos métodos de salvar, excluir e editar.

    @Transactional
    public Lembrete salvar(Lembrete lembrete, Usuario usuario) {
        lembrete.setUsuario(usuario);
        return lembreteRepository.save(lembrete);
    }

    public Lembrete buscarPorId(UUID id, Usuario usuario) {
        Lembrete lembrete = lembreteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        // Verificação 
        if (!lembrete.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Você não tem permissão para este recurso.");
        }
        return lembrete;
    }

    @Transactional
    public void excluir(UUID id, Usuario usuario) {
        Lembrete lembrete = buscarPorId(id, usuario);
        lembreteRepository.delete(lembrete);
    }

    @Transactional
    public Lembrete editar(UUID id, Lembrete dadosNovos, Usuario usuario) {
        Lembrete existente = buscarPorId(id, usuario);

        existente.setNome(dadosNovos.getNome());
        existente.setDescricao(dadosNovos.getDescricao());
        existente.setDataHora(dadosNovos.getDataHora());
        existente.setPrioridade(dadosNovos.getPrioridade());
        existente.setRealizada(dadosNovos.isRealizada());

        return lembreteRepository.save(existente);
    }
}
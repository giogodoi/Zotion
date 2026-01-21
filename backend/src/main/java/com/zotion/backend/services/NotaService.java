package com.zotion.backend.services;

import com.zotion.backend.models.Nota;
import com.zotion.backend.models.Usuario;
import com.zotion.backend.repositories.NotaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class NotaService {

    private final NotaRepository notaRepository;

    public NotaService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }

    public List<Nota> listarPorUsuario(Usuario usuario) {
        // Ordenação didática: Fixadas no topo, depois por data de criação decrescente
        Sort ordenacao = Sort.by("fixada").descending()
                             .and(Sort.by("dataCriacao").descending());

        return notaRepository.findAllByUsuario(usuario, ordenacao);
    }

    @Transactional
    public Nota salvar(Nota nota, Usuario usuario) {
        nota.setUsuario(usuario);
        return notaRepository.save(nota);
    }

    public Nota buscarPorId(UUID id, Usuario usuario) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada."));

        if (!nota.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta nota.");
        }
        return nota;
    }

    @Transactional
    public void excluir(UUID id, Usuario usuario) {
        Nota nota = buscarPorId(id, usuario);
        notaRepository.delete(nota);
    }

    @Transactional
    public Nota editar(UUID id, Nota novosDados, Usuario usuario) {
        Nota existente = buscarPorId(id, usuario);

        existente.setTitulo(novosDados.getTitulo());
        existente.setConteudo(novosDados.getConteudo());
        existente.setCor(novosDados.getCor());
        existente.setFixada(novosDados.isFixada());

        return notaRepository.save(existente);
    }
}
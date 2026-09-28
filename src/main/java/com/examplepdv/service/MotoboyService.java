package com.examplepdv.service;

import com.examplepdv.model.Motoboy;
import com.examplepdv.repository.MotoboyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MotoboyService {

    private final MotoboyRepository motoboyRepository;

    public MotoboyService(MotoboyRepository motoboyRepository) {
        this.motoboyRepository = motoboyRepository;
    }

    public List<Motoboy> listarAtivos() { return motoboyRepository.findByAtivoTrueOrderByNomeAsc(); }
    public List<Motoboy> listarTodos() { return motoboyRepository.findAll(); }
    public Optional<Motoboy> buscarPorId(Long id) { return motoboyRepository.findById(id); }

    @Transactional
    public Motoboy salvar(Motoboy motoboy) { return motoboyRepository.save(motoboy); }

    @Transactional
    public void alternarStatus(Long id) {
        motoboyRepository.findById(id).ifPresent(m -> {
            m.setAtivo(!m.isAtivo());
            motoboyRepository.save(m);
        });
    }

    @Transactional
    public void excluir(Long id) { motoboyRepository.deleteById(id); }
}

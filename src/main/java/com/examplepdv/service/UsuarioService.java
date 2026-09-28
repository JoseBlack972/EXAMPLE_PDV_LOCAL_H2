package com.examplepdv.service;

import com.examplepdv.model.Usuario;
import com.examplepdv.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("Nome de usuário já cadastrado!");
            }
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else {
            Usuario existente = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            
            if (!existente.getUsername().equals(usuario.getUsername()) &&
                usuarioRepository.existsByUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("Nome de usuário já cadastrado!");
            }

            if (usuario.getSenha() != null && !usuario.getSenha().trim().isEmpty() &&
                !usuario.getSenha().equals(existente.getSenha())) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            } else {
                usuario.setSenha(existente.getSenha());
            }
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id, String usuarioLogado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (usuario.getUsername().equalsIgnoreCase(usuarioLogado)) {
            throw new IllegalArgumentException("Você não pode excluir o seu próprio usuário logado!");
        }
        usuarioRepository.delete(usuario);
    }
}

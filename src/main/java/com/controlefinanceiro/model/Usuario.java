package com.controlefinanceiro.model;

import com.controlefinanceiro.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "plano_atual", length = 50)
    private String planoAtual = "FREE";

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "codigo_recuperacao")
    private String codigoRecuperacao;

    @Column(name = "validade_codigo")
    private LocalDateTime validadeCodigo;

    // 1. O Vínculo: Se for nulo, este utilizador é o dono. Se tiver ID, é um colaborador.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_conta_id")
    private Usuario donoDaConta;

    // 2. Múltiplos Papéis: Transformamos o Role único numa lista/set de Roles
    @ElementCollection(fetch = FetchType.EAGER) // EAGER para o Spring Security carregar logo as permissões
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private Set<Role> roles = new HashSet<>();

    // --- MÉTODOS OBRIGATÓRIOS DO SPRING SECURITY (USER DETAILS) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Pega todos os papéis do utilizador (ex: USER, COLABORADOR_LEITURA) e transforma em Authorities
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    @Override
    public String getUsername() {
        return this.email; // O Spring usa o conceito de "Username", mas para nós é o e-mail
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
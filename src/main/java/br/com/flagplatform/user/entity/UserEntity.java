package br.com.flagplatform.user.entity;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        }
)
public class UserEntity extends BaseEntity {
    private String name;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private UserStatus status;

    private UserRole role;

    /**
     * UID do Firebase Auth. Usado para vincular o usuário PostgreSQL à conta Firebase
     * durante a migração de autenticação (JWT custom → Firebase ID tokens).
     * <p>
     * Usuários existentes (sem firebaseUid) continuam autenticando via JWT legacy.
     * Novos usuários ou migrados populam este campo.
     */
    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    /**
     * ID da organização à qual o usuário pertence. Usado para controle de acesso
     * no contexto de organização e sincronizado com as custom claims do Firebase
     * (organization_id).
     */
    @Column(name = "organization_id")
    private UUID organizationId;

    /**
     * ID do clube à qual o usuário está vinculado. Usado para controle de acesso
     * no contexto de clube e sincronizado com as custom claims do Firebase (club_id).
     */
    @Column(name = "club_id")
    private UUID clubId;

    /**
     * Skills do usuário no contexto do Flag Football. Preenchido via custom claims
     * do Firebase (athlete, coach, referee, manager) e usado para controle de
     * permissões finer-grained além do role.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private List<String> skills;
}

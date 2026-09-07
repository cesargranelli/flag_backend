package br.com.flagplatform.user.entity;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

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
                ),
                @UniqueConstraint(
                        name = "users_firebase_uid_key",
                        columnNames = "firebase_uid"
                )
        }
)
public class UserEntity extends BaseEntity {
    private String name;

    private String email;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "club_id")
    private UUID clubId;

    @Column(nullable = false)
    private UserStatus status;

    private UserRole role;
}

package co.innovaciones.lulo.domain

import co.innovaciones.lulo.model.UsersRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.OffsetDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener


@Entity
@Table(name = "\"user\"")
@EntityListeners(AuditingEntityListener::class)
class User {

    @Id
    @Column(
        nullable = false,
        updatable = false
    )
    @SequenceGenerator(
        name = "primary_sequence",
        sequenceName = "primary_sequence",
        allocationSize = 1,
        initialValue = 10000
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "primary_sequence"
    )
    var id: Long? = null

    @Column(
        nullable = false,
        length = 50
    )
    var firstName: String? = null

    @Column(length = 50)
    var lastName: String? = null

    @Column(
        nullable = false,
        unique = true,
        length = 50
    )
    var username: String? = null

    @Column(
        nullable = false,
        unique = true,
        length = 100
    )
    var email: String? = null

    @Column(length = 50)
    var phone: String? = null

    @Column(nullable = false)
    var password: String? = null

    @Column
    var lastLogin: OffsetDateTime? = null

    @Column(nullable = false)
    var enabled: Boolean? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: UsersRole? = null

    @CreatedDate
    @Column(
        nullable = false,
        updatable = false
    )
    var dateCreated: OffsetDateTime? = null

    @LastModifiedDate
    @Column(nullable = false)
    var lastUpdated: OffsetDateTime? = null

}

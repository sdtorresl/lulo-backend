package co.innovaciones.lulo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import java.time.OffsetDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener


@Entity
@EntityListeners(AuditingEntityListener::class)
class Business {

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

    @Column(nullable = false)
    var name: String? = null

    @Column(columnDefinition = "text")
    var description: String? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "logo_id",
        nullable = false,
        unique = true
    )
    var logo: File? = null

    @OneToMany(mappedBy = "brand")
    var users: MutableSet<BusinessUser>? = null

    @ManyToMany
    @JoinTable(
        name = "customer_brand",
        joinColumns = [
            JoinColumn(name = "business_id")
        ],
        inverseJoinColumns = [
            JoinColumn(name = "customer_id")
        ]
    )
    var customers: MutableSet<Customer>? = null

    @OneToMany(mappedBy = "business")
    var categories: MutableSet<Category>? = null

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

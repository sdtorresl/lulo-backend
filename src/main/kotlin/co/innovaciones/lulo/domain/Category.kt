package co.innovaciones.lulo.domain

import co.innovaciones.lulo.model.CategoryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import java.time.OffsetDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener


@Entity
@EntityListeners(AuditingEntityListener::class)
class Category {

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

    @Column
    @Enumerated(EnumType.STRING)
    var type: CategoryType? = null

    @ManyToMany
    @JoinTable(
        name = "category_producy",
        joinColumns = [
            JoinColumn(name = "category_id")
        ],
        inverseJoinColumns = [
            JoinColumn(name = "product_id")
        ]
    )
    var products: MutableSet<Product>? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "file_id",
        nullable = false,
        unique = true
    )
    var `file`: File? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    var business: Business? = null

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

package co.innovaciones.lulo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.OffsetDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener


@Entity
@Table(name = "\"order\"")
@EntityListeners(AuditingEntityListener::class)
class Order {

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
        unique = true
    )
    var sessionId: String? = null

    @Column(nullable = false)
    var subtotal: Double? = null

    @Column
    var itemDiscount: Double? = null

    @Column(nullable = false)
    var discount: Double? = null

    @Column(nullable = false)
    var tax: Double? = null

    @Column(nullable = false)
    var shipping: Double? = null

    @Column(nullable = false)
    var total: Double? = null

    @Column(columnDefinition = "text")
    var content: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "customer_id",
        nullable = false
    )
    var customer: Customer? = null

    @OneToMany(mappedBy = "order")
    var items: MutableSet<OrderItem>? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "delivery_address_id",
        unique = true
    )
    var deliveryAddress: Address? = null

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

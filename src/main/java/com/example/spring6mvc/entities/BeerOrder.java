package com.example.spring6mvc.entities;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
public class BeerOrder {

    public BeerOrder(UUID id, String customerRef, LocalDateTime createDate, LocalDateTime lastModifiedDate, Integer version, Customer customer, Set<BeerOrderLine> lines, BeerOrderShipment orderShipment) {
        this.id = id;
        this.customerRef = customerRef;
        this.createdDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
        this.version = version;
        this.setCustomer(customer);
        this.lines = lines;
        this.setOrderShipment(orderShipment);
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;
    private String customerRef;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
    @Version
    private Integer version;

    public boolean isNew() {
        return this.getId() == null;
    }

    @ManyToOne
    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.getOrders().add(this);
    }

    @OneToMany(mappedBy = "order")
    private Set<BeerOrderLine> lines;
    @OneToOne(cascade = CascadeType.PERSIST)
    private BeerOrderShipment orderShipment;

    public void setOrderShipment(BeerOrderShipment orderShipment) {
        this.orderShipment = orderShipment;
        orderShipment.setOrder(this);
    }
}

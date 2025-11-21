package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Supplier Entity - Supplier Information
 *
 * Stores details of companies that supply goods as per SRS Table 3.
 * Implements FR-INV-01 (Supplier Management) requirements.
 *
 * A Supplier can provide multiple Products and Goods Received Notes (GRNs).
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "Supplier")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the supplier.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplierId")
    private Long supplierId;

    /**
     * Legal name of the supplier company.
     * Required field, maximum 100 characters.
     */
    @Column(name = "supplierName", nullable = false, length = 100)
    private String supplierName;

    /**
     * Primary contact person at the supplier.
     * Optional field, maximum 100 characters.
     */
    @Column(name = "contactPerson", length = 100)
    private String contactPerson;

    /**
     * Contact phone number.
     * Optional field, maximum 20 characters.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Physical address of the supplier.
     * Optional field, maximum 255 characters.
     */
    @Column(name = "address", length = 255)
    private String address;

    /**
     * One-to-Many relationship with Product.
     * A supplier can provide multiple products.
     * Mapped by the 'supplier' field in Product entity.
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    /**
     * One-to-Many relationship with GRN.
     * A supplier can have multiple Goods Received Notes.
     * Mapped by the 'supplier' field in GRN entity.
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GRN> grns = new ArrayList<>();

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public Supplier() {
    }

    /**
     * Constructor with required field.
     *
     * @param supplierName Legal name of the supplier
     */
    public Supplier(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * Constructor with commonly used fields.
     *
     * @param supplierName  Legal name of the supplier
     * @param contactPerson Primary contact person
     * @param phone         Contact phone number
     */
    public Supplier(String supplierName, String contactPerson, String phone) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
    }

    /**
     * Constructor with all fields.
     *
     * @param supplierName  Legal name of the supplier
     * @param contactPerson Primary contact person
     * @param phone         Contact phone number
     * @param address       Physical address
     */
    public Supplier(String supplierName, String contactPerson, String phone, String address) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
    }

    // ==================== Getters and Setters ====================

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<GRN> getGrns() {
        return grns;
    }

    public void setGrns(List<GRN> grns) {
        this.grns = grns;
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(supplierId, supplier.supplierId) &&
               Objects.equals(supplierName, supplier.supplierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, supplierName);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

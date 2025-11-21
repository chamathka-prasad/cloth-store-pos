package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Customer Entity - Customer Information (CRM)
 *
 * Stores customer information for CRM and sales linking as per SRS Table 2.
 * Implements FR-CUST (Customer Management) requirements.
 *
 * As per FR-CUST-02: phoneNumber is unique and serves as the primary identifier.
 * As per FR-CUST-05: Automatically tracks total lifetime purchases and visit count.
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "Customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the customer.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerId")
    private Long customerId;

    /**
     * Customer's full name.
     * Required field, maximum 100 characters.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Customer's phone number.
     * This is the primary customer identifier (FR-CUST-02).
     * Must be unique, maximum 20 characters.
     */
    @Column(name = "phoneNumber", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    /**
     * Customer's email address.
     * Optional field, maximum 100 characters.
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * Total lifetime value of the customer (sum of all purchases).
     * Automatically updated when sales are processed (FR-CUST-05).
     * Precision: 10 digits, 2 decimal places.
     * Default: 0.00
     */
    @Column(name = "totalPurchases", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    /**
     * Total number of sales/visits by this customer.
     * Automatically updated when sales are processed (FR-CUST-05).
     * Default: 0
     */
    @Column(name = "visitCount", nullable = false)
    private Integer visitCount = 0;

    /**
     * One-to-Many relationship with Sale.
     * A customer can have multiple sales (purchase history).
     * Mapped by the 'customer' field in Sale entity.
     * Lazy loading for performance (FR-CUST-04).
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sale> sales = new ArrayList<>();

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public Customer() {
    }

    /**
     * Constructor with required fields.
     *
     * @param name        Customer's full name
     * @param phoneNumber Customer's phone number (unique identifier)
     */
    public Customer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor with all basic fields.
     *
     * @param name        Customer's full name
     * @param phoneNumber Customer's phone number (unique identifier)
     * @param email       Customer's email address (optional)
     */
    public Customer(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // ==================== Getters and Setters ====================

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(BigDecimal totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Adds a purchase amount to the customer's total and increments visit count.
     * Called automatically when a sale is processed (FR-CUST-05).
     *
     * @param amount The sale amount to add
     */
    public void addPurchase(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.totalPurchases = this.totalPurchases.add(amount);
            this.visitCount++;
        }
    }

    /**
     * Subtracts a returned amount from the customer's total and decrements visit count.
     * Called when a return is processed.
     *
     * @param amount The return amount to subtract
     */
    public void subtractPurchase(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.totalPurchases = this.totalPurchases.subtract(amount);
            if (this.visitCount > 0) {
                this.visitCount--;
            }
        }
    }

    /**
     * Calculates the average purchase value.
     *
     * @return Average purchase amount, or 0 if no visits
     */
    public BigDecimal getAveragePurchaseValue() {
        if (visitCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalPurchases.divide(
            BigDecimal.valueOf(visitCount),
            2,
            BigDecimal.ROUND_HALF_UP
        );
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) &&
               Objects.equals(phoneNumber, customer.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, phoneNumber);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", totalPurchases=" + totalPurchases +
                ", visitCount=" + visitCount +
                '}';
    }
}

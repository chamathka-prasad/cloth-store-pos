package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sale Entity - Sales Transaction Header
 *
 * Header table for a customer sales transaction as per SRS Table 8.
 * Implements FR-POS (Point of Sale) requirements.
 *
 * A Sale represents a complete checkout transaction, containing:
 * - Sale timestamp
 * - Cashier (User)
 * - Customer (optional)
 * - Total amount
 * - Discount amount
 * - Payment type
 * - Line items (SaleItems)
 *
 * As per FR-POS-10: Upon checkout, stock quantities are deducted atomically.
 * As per UC-01: This is the core entity for the sales transaction process.
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "Sale")
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique receipt ID for the sale.
     * This is the number printed on the receipt.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saleId")
    private Long saleId;

    /**
     * The exact date and time of the sale.
     * Automatically set to current timestamp.
     * Required field.
     */
    @Column(name = "saleTimestamp", nullable = false)
    private LocalDateTime saleTimestamp;

    /**
     * Many-to-One relationship with User.
     * The cashier who processed this sale.
     * Foreign key to User table.
     * Required field.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;

    /**
     * Many-to-One relationship with Customer.
     * The customer who made the purchase.
     * Foreign key to Customer table.
     * Optional - nullable for walk-in customers (FR-CUST-05).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    private Customer customer;

    /**
     * Final total amount paid by the customer.
     * This is AFTER discounts are applied.
     * Total = Sum(SaleItem.priceAtSale * quantity) - discountAmount
     * Required field. Precision: 10 digits, 2 decimal places.
     */
    @Column(name = "totalAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Total discount given on this sale.
     * Can be applied to individual items or the entire bill (FR-POS-06).
     * Default: 0.00. Precision: 10 digits, 2 decimal places.
     */
    @Column(name = "discountAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Payment method used.
     * Valid values: "Cash", "Card", "Split"
     * As per FR-POS-07, FR-POS-08.
     * Required field, maximum 50 characters.
     */
    @Column(name = "paymentType", nullable = false, length = 50)
    private String paymentType;

    /**
     * One-to-Many relationship with SaleItem.
     * The line items for this sale.
     * Mapped by the 'sale' field in SaleItem entity.
     * Cascade operations (e.g., delete sale deletes all items).
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleItem> saleItems = new ArrayList<>();

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public Sale() {
        this.saleTimestamp = LocalDateTime.now();
    }

    /**
     * Constructor with required fields.
     *
     * @param user        The cashier processing the sale
     * @param paymentType Payment method ("Cash", "Card", "Split")
     */
    public Sale(User user, String paymentType) {
        this.user = user;
        this.paymentType = paymentType;
        this.saleTimestamp = LocalDateTime.now();
    }

    /**
     * Constructor with commonly used fields.
     *
     * @param user        The cashier processing the sale
     * @param customer    The customer (can be null)
     * @param paymentType Payment method ("Cash", "Card", "Split")
     */
    public Sale(User user, Customer customer, String paymentType) {
        this.user = user;
        this.customer = customer;
        this.paymentType = paymentType;
        this.saleTimestamp = LocalDateTime.now();
    }

    // ==================== Getters and Setters ====================

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public LocalDateTime getSaleTimestamp() {
        return saleTimestamp;
    }

    public void setSaleTimestamp(LocalDateTime saleTimestamp) {
        this.saleTimestamp = saleTimestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Adds a sale item to this sale.
     * Maintains bidirectional relationship.
     * Recalculates total amount.
     *
     * @param saleItem The sale item to add
     */
    public void addSaleItem(SaleItem saleItem) {
        saleItems.add(saleItem);
        saleItem.setSale(this);
        recalculateTotalAmount();
    }

    /**
     * Removes a sale item from this sale.
     * Maintains bidirectional relationship.
     * Recalculates total amount.
     *
     * @param saleItem The sale item to remove
     */
    public void removeSaleItem(SaleItem saleItem) {
        saleItems.remove(saleItem);
        saleItem.setSale(null);
        recalculateTotalAmount();
    }

    /**
     * Calculates the subtotal (before discount).
     * Subtotal = Sum of (priceAtSale * quantitySold) for all items.
     *
     * @return Subtotal amount
     */
    public BigDecimal calculateSubtotal() {
        return saleItems.stream()
            .map(item -> item.getPriceAtSale()
                .multiply(BigDecimal.valueOf(item.getQuantitySold())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Recalculates the total amount for this sale.
     * Total = Subtotal - Discount
     * This is the final amount the customer pays.
     */
    public void recalculateTotalAmount() {
        BigDecimal subtotal = calculateSubtotal();
        this.totalAmount = subtotal.subtract(discountAmount);
    }

    /**
     * Applies a discount to the entire sale.
     * As per FR-POS-06: Discount can be percentage or fixed amount.
     *
     * @param discount The discount amount to apply
     */
    public void applyDiscount(BigDecimal discount) {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) >= 0) {
            this.discountAmount = discount;
            recalculateTotalAmount();
        }
    }

    /**
     * Checks if this sale has a customer associated.
     *
     * @return true if customer is linked, false otherwise
     */
    public boolean hasCustomer() {
        return this.customer != null;
    }

    /**
     * Gets the number of items in this sale.
     *
     * @return Number of line items
     */
    public int getItemCount() {
        return saleItems.size();
    }

    /**
     * Gets the total quantity of all items sold.
     *
     * @return Total quantity
     */
    public int getTotalQuantity() {
        return saleItems.stream()
            .mapToInt(SaleItem::getQuantitySold)
            .sum();
    }

    /**
     * Checks if payment was made in cash.
     *
     * @return true if payment type is "Cash"
     */
    public boolean isCashPayment() {
        return "Cash".equalsIgnoreCase(this.paymentType);
    }

    /**
     * Checks if payment was made by card.
     *
     * @return true if payment type is "Card"
     */
    public boolean isCardPayment() {
        return "Card".equalsIgnoreCase(this.paymentType);
    }

    /**
     * Checks if payment was split (cash + card).
     *
     * @return true if payment type is "Split"
     */
    public boolean isSplitPayment() {
        return "Split".equalsIgnoreCase(this.paymentType);
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return Objects.equals(saleId, sale.saleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId);
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", timestamp=" + saleTimestamp +
                ", totalAmount=" + totalAmount +
                ", discountAmount=" + discountAmount +
                ", paymentType='" + paymentType + '\'' +
                ", itemCount=" + getItemCount() +
                ", hasCustomer=" + hasCustomer() +
                '}';
    }
}

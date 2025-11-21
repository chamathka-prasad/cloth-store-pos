package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * SaleItem Entity - Sale Transaction Line Items
 *
 * Line items for a specific Sale as per SRS Table 9.
 * Each SaleItem represents one product variant being sold in a transaction.
 *
 * CRITICAL FIELD: priceAtSale
 * This is a snapshot of the selling price at the time of sale.
 * Why snapshot the price?
 * - Prices may change over time
 * - Historical accuracy for reports
 * - Essential for profit calculation (priceAtSale - costPrice)
 * - Receipt reprinting shows correct historical price
 *
 * As per FR-POS: Sale items are added to the cart and processed at checkout.
 * As per FR-RPT-03: priceAtSale is used with costPrice for profit calculation.
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "SaleItem")
public class SaleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the sale line item.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saleItemId")
    private Long saleItemId;

    /**
     * Many-to-One relationship with Sale.
     * The sale transaction this item belongs to.
     * Foreign key to Sale table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", referencedColumnName = "saleId", nullable = false)
    private Sale sale;

    /**
     * Many-to-One relationship with ProductVariant.
     * The specific variant that was sold.
     * Foreign key to ProductVariant table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", referencedColumnName = "variantId", nullable = false)
    private ProductVariant variant;

    /**
     * The quantity of the item sold in this transaction.
     * This quantity is deducted from ProductVariant.quantityInStock (FR-POS-10).
     * Required field. Must be positive.
     */
    @Column(name = "quantitySold", nullable = false)
    private Integer quantitySold;

    /**
     * CRUCIAL: A snapshot of the selling price at the time of sale.
     *
     * Why this is critical:
     * - Historical Accuracy: Prices change over time. This preserves what was actually paid.
     * - Profit Calculation: profit = (priceAtSale - costPrice) * quantitySold (FR-RPT-03)
     * - Receipt Reprinting: Shows the correct price that was charged
     * - Sales Analysis: Compare actual sold prices vs. current prices
     *
     * This is copied from ProductVariant.sellingPrice at the moment of sale,
     * but stored separately so that future price changes don't affect historical data.
     *
     * Required field. Precision: 10 digits, 2 decimal places.
     */
    @Column(name = "priceAtSale", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtSale;

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public SaleItem() {
    }

    /**
     * Constructor with all required fields.
     *
     * @param sale         The sale this item belongs to
     * @param variant      The product variant being sold
     * @param quantitySold Quantity sold
     * @param priceAtSale  Price at the time of sale (snapshot)
     */
    public SaleItem(Sale sale, ProductVariant variant, Integer quantitySold, BigDecimal priceAtSale) {
        this.sale = sale;
        this.variant = variant;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    /**
     * Convenience constructor that auto-captures the current selling price.
     * Use this when adding items to a sale - it automatically snapshots the price.
     *
     * @param sale         The sale this item belongs to
     * @param variant      The product variant being sold
     * @param quantitySold Quantity sold
     */
    public SaleItem(Sale sale, ProductVariant variant, Integer quantitySold) {
        this.sale = sale;
        this.variant = variant;
        this.quantitySold = quantitySold;
        // Snapshot the current selling price
        this.priceAtSale = variant.getSellingPrice();
    }

    // ==================== Getters and Setters ====================

    public Long getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(Long saleItemId) {
        this.saleItemId = saleItemId;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(BigDecimal priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Calculates the line total (subtotal for this item).
     * Line Total = priceAtSale * quantitySold
     *
     * @return Total amount for this line item
     */
    public BigDecimal getLineTotal() {
        return priceAtSale.multiply(BigDecimal.valueOf(quantitySold));
    }

    /**
     * Gets the item code (SKU) of the variant.
     * Convenience method to avoid null checks.
     *
     * @return Item code or null if variant not set
     */
    public String getItemCode() {
        return variant != null ? variant.getItemCode() : null;
    }

    /**
     * Gets the display name of the variant.
     * Convenience method to avoid null checks.
     *
     * @return Display name or null if variant not set
     */
    public String getVariantDisplayName() {
        return variant != null ? variant.getDisplayName() : null;
    }

    /**
     * Gets the product name (from the master product).
     * Convenience method for display purposes.
     *
     * @return Product name or null if not available
     */
    public String getProductName() {
        if (variant != null && variant.getProduct() != null) {
            return variant.getProduct().getName();
        }
        return null;
    }

    /**
     * Calculates the profit for this line item.
     * IMPORTANT: This requires joining with GRNItem to get the costPrice.
     * This method is a placeholder - actual profit calculation should be done
     * in the Service layer with proper cost price retrieval.
     *
     * Profit = (priceAtSale - costPrice) * quantitySold
     *
     * Note: Cost price comes from the most recent GRNItem for this variant.
     * This is a business decision - you could also use average cost or FIFO.
     *
     * @param costPrice The cost price from GRNItem
     * @return Profit amount for this line item
     */
    public BigDecimal calculateProfit(BigDecimal costPrice) {
        if (costPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal profitPerUnit = priceAtSale.subtract(costPrice);
        return profitPerUnit.multiply(BigDecimal.valueOf(quantitySold));
    }

    /**
     * Checks if the price has changed since this sale.
     * Compares the historical priceAtSale with the current variant selling price.
     *
     * @return true if price has changed, false otherwise
     */
    public boolean hasPriceChanged() {
        if (variant == null) {
            return false;
        }
        return !priceAtSale.equals(variant.getSellingPrice());
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleItem saleItem = (SaleItem) o;
        return Objects.equals(saleItemId, saleItem.saleItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleItemId);
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "saleItemId=" + saleItemId +
                ", itemCode='" + getItemCode() + '\'' +
                ", quantitySold=" + quantitySold +
                ", priceAtSale=" + priceAtSale +
                ", lineTotal=" + getLineTotal() +
                '}';
    }
}

package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * GRNItem Entity - Goods Received Note Line Items
 *
 * Line items for a specific GRN as per SRS Table 7.
 * Each GRNItem represents one product variant being received from a supplier.
 *
 * CRITICAL FIELD: costPrice
 * This is where the supplier's cost price is recorded, which is essential for:
 * - Profit calculation (FR-RPT-03)
 * - Inventory valuation
 * - Supplier comparison
 *
 * As per FR-GRN-02: Admin specifies quantityReceived and costPrice per unit.
 * As per FR-GRN-06: Upon GRN confirmation, quantityReceived is added to ProductVariant stock.
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "GRNItem")
public class GRNItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the GRN line item.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grnItemId")
    private Long grnItemId;

    /**
     * Many-to-One relationship with GRN.
     * The GRN header this item belongs to.
     * Foreign key to GRN table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", referencedColumnName = "grnId", nullable = false)
    private GRN grn;

    /**
     * Many-to-One relationship with ProductVariant.
     * The specific variant being received.
     * Foreign key to ProductVariant table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", referencedColumnName = "variantId", nullable = false)
    private ProductVariant variant;

    /**
     * The quantity of the item received from the supplier.
     * This quantity will be added to ProductVariant.quantityInStock when GRN is confirmed.
     * As per FR-GRN-02 and FR-GRN-06.
     * Required field. Must be positive.
     */
    @Column(name = "quantityReceived", nullable = false)
    private Integer quantityReceived;

    /**
     * CRUCIAL: The cost per unit from the supplier.
     * This is what the store PAYS the supplier (not what the customer pays).
     *
     * Why this is critical:
     * - Profit Calculation: profit = (priceAtSale - costPrice) * quantity
     * - Inventory Valuation: total inventory value at cost
     * - Supplier Comparison: compare costs from different suppliers
     *
     * As per FR-GRN-02 and FR-RPT-03.
     * Required field. Precision: 10 digits, 2 decimal places.
     */
    @Column(name = "costPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public GRNItem() {
    }

    /**
     * Constructor with all required fields.
     *
     * @param grn              The GRN this item belongs to
     * @param variant          The product variant being received
     * @param quantityReceived Quantity received from supplier
     * @param costPrice        Cost per unit from supplier
     */
    public GRNItem(GRN grn, ProductVariant variant, Integer quantityReceived, BigDecimal costPrice) {
        this.grn = grn;
        this.variant = variant;
        this.quantityReceived = quantityReceived;
        this.costPrice = costPrice;
    }

    // ==================== Getters and Setters ====================

    public Long getGrnItemId() {
        return grnItemId;
    }

    public void setGrnItemId(Long grnItemId) {
        this.grnItemId = grnItemId;
    }

    public GRN getGrn() {
        return grn;
    }

    public void setGrn(GRN grn) {
        this.grn = grn;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Calculates the total cost for this line item.
     * Total Cost = quantityReceived * costPrice
     *
     * @return Total cost for this item
     */
    public BigDecimal getTotalCost() {
        return costPrice.multiply(BigDecimal.valueOf(quantityReceived));
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

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GRNItem grnItem = (GRNItem) o;
        return Objects.equals(grnItemId, grnItem.grnItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grnItemId);
    }

    @Override
    public String toString() {
        return "GRNItem{" +
                "grnItemId=" + grnItemId +
                ", itemCode='" + getItemCode() + '\'' +
                ", quantityReceived=" + quantityReceived +
                ", costPrice=" + costPrice +
                ", totalCost=" + getTotalCost() +
                '}';
    }
}

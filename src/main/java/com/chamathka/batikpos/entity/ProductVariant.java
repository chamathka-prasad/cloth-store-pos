package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ProductVariant Entity - Stocked Items (SKU)
 *
 * The actual, sellable item in inventory as per SRS Table 5.
 * This is what has stock and is sold at POS.
 * Implements FR-INV-03, FR-INV-04, FR-INV-06, FR-INV-07 requirements.
 *
 * Examples:
 * - Master Product: "Batik Shirt"
 * - Variants: "SHIRT-RED-M", "SHIRT-RED-L", "SHIRT-BLUE-M"
 *
 * Each variant has its own:
 * - Unique SKU (itemCode)
 * - Attributes (Size, Color)
 * - Selling price
 * - Stock quantity
 * - Low stock threshold
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "ProductVariant")
public class ProductVariant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the variant.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variantId")
    private Long variantId;

    /**
     * Many-to-One relationship with Product.
     * The master product this variant belongs to.
     * Foreign key to Product table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "productId", nullable = false)
    private Product product;

    /**
     * The SKU (Stock Keeping Unit).
     * A unique alphanumeric code (e.g., "SHIRT-RED-M").
     * This is THE unique identifier for inventory operations (FR-INV-04).
     * Required, unique, maximum 50 characters.
     */
    @Column(name = "itemCode", nullable = false, unique = true, length = 50)
    private String itemCode;

    /**
     * Size attribute (e.g., "S", "M", "L", "XL", "Free Size").
     * Optional field, maximum 50 characters.
     */
    @Column(name = "attribute_Size", length = 50)
    private String attributeSize;

    /**
     * Color attribute (e.g., "Red", "Blue", "Black", "Multi-color").
     * Optional field, maximum 50 characters.
     */
    @Column(name = "attribute_Color", length = 50)
    private String attributeColor;

    /**
     * The retail selling price (what the customer pays).
     * This is the base price before any discounts.
     * Required field. Precision: 10 digits, 2 decimal places.
     */
    @Column(name = "sellingPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    /**
     * The current, real-time stock level (FR-INV-04).
     * This is THE critical field for inventory management.
     * Updated by:
     * - GRN confirmation (adds stock)
     * - Sale checkout (deducts stock)
     * - Returns (adds stock back)
     * - Manual adjustments (FR-INV-06)
     * Required field. Default: 0
     */
    @Column(name = "quantityInStock", nullable = false)
    private Integer quantityInStock = 0;

    /**
     * The stock level at which low stock alerts trigger (FR-INV-07).
     * When quantityInStock <= lowStockThreshold, system shows alert.
     * Required field. Default: 5
     */
    @Column(name = "lowStockThreshold", nullable = false)
    private Integer lowStockThreshold = 5;

    /**
     * One-to-Many relationship with GRNItem.
     * Tracks all stock intake records for this variant.
     * Mapped by the 'variant' field in GRNItem entity.
     */
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GRNItem> grnItems = new ArrayList<>();

    /**
     * One-to-Many relationship with SaleItem.
     * Tracks all sales of this variant.
     * Mapped by the 'variant' field in SaleItem entity.
     */
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleItem> saleItems = new ArrayList<>();

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public ProductVariant() {
    }

    /**
     * Constructor with required fields.
     *
     * @param product      The master product
     * @param itemCode     Unique SKU
     * @param sellingPrice Retail price
     */
    public ProductVariant(Product product, String itemCode, BigDecimal sellingPrice) {
        this.product = product;
        this.itemCode = itemCode;
        this.sellingPrice = sellingPrice;
    }

    /**
     * Constructor with commonly used fields.
     *
     * @param product       The master product
     * @param itemCode      Unique SKU
     * @param attributeSize Size (e.g., "M", "L")
     * @param attributeColor Color (e.g., "Red", "Blue")
     * @param sellingPrice  Retail price
     */
    public ProductVariant(Product product, String itemCode, String attributeSize,
                          String attributeColor, BigDecimal sellingPrice) {
        this.product = product;
        this.itemCode = itemCode;
        this.attributeSize = attributeSize;
        this.attributeColor = attributeColor;
        this.sellingPrice = sellingPrice;
    }

    // ==================== Getters and Setters ====================

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getAttributeSize() {
        return attributeSize;
    }

    public void setAttributeSize(String attributeSize) {
        this.attributeSize = attributeSize;
    }

    public String getAttributeColor() {
        return attributeColor;
    }

    public void setAttributeColor(String attributeColor) {
        this.attributeColor = attributeColor;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public List<GRNItem> getGrnItems() {
        return grnItems;
    }

    public void setGrnItems(List<GRNItem> grnItems) {
        this.grnItems = grnItems;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Adds stock quantity (called during GRN confirmation).
     * As per FR-GRN-06: Stock is added when GRN is confirmed.
     *
     * @param quantity The quantity to add (must be positive)
     * @throws IllegalArgumentException if quantity is negative
     */
    public void addStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Cannot add negative stock quantity");
        }
        this.quantityInStock += quantity;
    }

    /**
     * Deducts stock quantity (called during sale checkout).
     * As per FR-POS-10: Stock is deducted when sale is finalized.
     *
     * @param quantity The quantity to deduct (must be positive)
     * @throws IllegalArgumentException if quantity is negative or exceeds stock
     */
    public void deductStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Cannot deduct negative stock quantity");
        }
        if (quantity > this.quantityInStock) {
            throw new IllegalArgumentException(
                "Cannot deduct " + quantity + " units. Only " + this.quantityInStock + " in stock."
            );
        }
        this.quantityInStock -= quantity;
    }

    /**
     * Checks if stock is available for a given quantity.
     * As per FR-POS-03: System must check stock before adding to cart.
     *
     * @param quantity The quantity to check
     * @return true if stock is available, false otherwise
     */
    public boolean isStockAvailable(int quantity) {
        return this.quantityInStock >= quantity;
    }

    /**
     * Checks if this variant is at or below low stock threshold.
     * As per FR-INV-07: System should alert when stock is low.
     *
     * @return true if stock is low, false otherwise
     */
    public boolean isLowStock() {
        return this.quantityInStock <= this.lowStockThreshold;
    }

    /**
     * Checks if this variant is out of stock.
     *
     * @return true if no stock available, false otherwise
     */
    public boolean isOutOfStock() {
        return this.quantityInStock == 0;
    }

    /**
     * Gets the full display name (Product Name + Attributes).
     * Example: "Batik Shirt (Red, Medium)"
     *
     * @return Formatted display name
     */
    public String getDisplayName() {
        StringBuilder name = new StringBuilder();
        if (product != null) {
            name.append(product.getName());
        }
        if (attributeColor != null || attributeSize != null) {
            name.append(" (");
            if (attributeColor != null) {
                name.append(attributeColor);
            }
            if (attributeSize != null) {
                if (attributeColor != null) {
                    name.append(", ");
                }
                name.append(attributeSize);
            }
            name.append(")");
        }
        return name.toString();
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariant that = (ProductVariant) o;
        return Objects.equals(variantId, that.variantId) &&
               Objects.equals(itemCode, that.itemCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId, itemCode);
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "variantId=" + variantId +
                ", itemCode='" + itemCode + '\'' +
                ", size='" + attributeSize + '\'' +
                ", color='" + attributeColor + '\'' +
                ", sellingPrice=" + sellingPrice +
                ", quantityInStock=" + quantityInStock +
                ", lowStock=" + isLowStock() +
                '}';
    }
}

package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Product Entity - Master Product Catalog
 *
 * Stores master product information as per SRS Table 4.
 * This is a container for product variants.
 * Implements FR-INV-02 (Master Product Management) requirements.
 *
 * A Product represents a general product type (e.g., "Batik Shirt").
 * The actual sellable items are ProductVariants (e.g., "Batik Shirt - Red - Medium").
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "Product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the master product.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private Long productId;

    /**
     * Product name (e.g., "Batik Shirt", "Batik Saree").
     * Required field, maximum 100 characters.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Product category (e.g., "Saree", "Shirt", "Sarong", "Dress").
     * Required field, maximum 50 characters.
     */
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /**
     * Many-to-One relationship with Supplier.
     * The default supplier for this product.
     * Foreign key to Supplier table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "supplierId")
    private Supplier supplier;

    /**
     * One-to-Many relationship with ProductVariant.
     * A master product can have multiple variants.
     * Mapped by the 'product' field in ProductVariant entity.
     * Cascade operations to variants (e.g., delete product deletes all variants).
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public Product() {
    }

    /**
     * Constructor with required fields.
     *
     * @param name     Product name
     * @param category Product category
     */
    public Product(String name, String category) {
        this.name = name;
        this.category = category;
    }

    /**
     * Constructor with all fields.
     *
     * @param name     Product name
     * @param category Product category
     * @param supplier Default supplier for this product
     */
    public Product(String name, String category, Supplier supplier) {
        this.name = name;
        this.category = category;
        this.supplier = supplier;
    }

    // ==================== Getters and Setters ====================

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Adds a variant to this product.
     * Maintains bidirectional relationship.
     *
     * @param variant The variant to add
     */
    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    /**
     * Removes a variant from this product.
     * Maintains bidirectional relationship.
     *
     * @param variant The variant to remove
     */
    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }

    /**
     * Gets the total number of variants for this product.
     *
     * @return Number of variants
     */
    public int getVariantCount() {
        return variants.size();
    }

    /**
     * Checks if this product has any variants.
     *
     * @return true if variants exist, false otherwise
     */
    public boolean hasVariants() {
        return !variants.isEmpty();
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId) &&
               Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", variantCount=" + getVariantCount() +
                '}';
    }
}

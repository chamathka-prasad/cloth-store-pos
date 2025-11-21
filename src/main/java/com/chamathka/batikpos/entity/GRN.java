package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GRN Entity - Goods Received Note (Stock Intake Header)
 *
 * Header table for a stock-in transaction from a supplier as per SRS Table 6.
 * Implements FR-GRN (Goods Received Note Management) requirements.
 *
 * A GRN formally records the receipt of goods from a supplier.
 * It contains:
 * - Supplier information
 * - Supplier's invoice reference
 * - Total cost
 * - Line items (GRNItems)
 * - Status (PENDING or CONFIRMED)
 *
 * As per FR-GRN-04/05: A GRN can be saved as PENDING (draft) or CONFIRMED (locked).
 * As per FR-GRN-06: Upon confirmation, stock quantities are updated atomically.
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "GRN")
public class GRN implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the GRN.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grnId")
    private Long grnId;

    /**
     * Many-to-One relationship with Supplier.
     * The supplier who sent the goods.
     * Foreign key to Supplier table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "supplierId", nullable = false)
    private Supplier supplier;

    /**
     * When the GRN was created/confirmed.
     * Automatically set to current timestamp.
     * Required field.
     */
    @Column(name = "grnTimestamp", nullable = false)
    private LocalDateTime grnTimestamp;

    /**
     * The supplier's invoice reference number.
     * This is the supplier's own document number.
     * Optional field, maximum 50 characters.
     */
    @Column(name = "supplierInvoiceNo", length = 50)
    private String supplierInvoiceNo;

    /**
     * Total cost of all items in this GRN.
     * Calculated as sum of (quantityReceived * costPrice) for all GRNItems.
     * As per FR-GRN-03: System must calculate total cost.
     * Required field. Precision: 10 digits, 2 decimal places.
     */
    @Column(name = "totalCost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    /**
     * Many-to-One relationship with User.
     * The Admin who processed this GRN.
     * Foreign key to User table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;

    /**
     * GRN status (FR-GRN-04, FR-GRN-05).
     * Valid values:
     * - "PENDING": Draft, can be edited
     * - "CONFIRMED": Locked, stock has been posted to inventory
     * Required field, maximum 20 characters.
     * Default: "PENDING"
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    /**
     * One-to-Many relationship with GRNItem.
     * The line items for this GRN.
     * Mapped by the 'grn' field in GRNItem entity.
     * Cascade operations (e.g., delete GRN deletes all items).
     */
    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GRNItem> grnItems = new ArrayList<>();

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public GRN() {
        this.grnTimestamp = LocalDateTime.now();
    }

    /**
     * Constructor with required fields.
     *
     * @param supplier The supplier providing the goods
     * @param user     The Admin user processing this GRN
     */
    public GRN(Supplier supplier, User user) {
        this.supplier = supplier;
        this.user = user;
        this.grnTimestamp = LocalDateTime.now();
    }

    /**
     * Constructor with commonly used fields.
     *
     * @param supplier          The supplier providing the goods
     * @param user              The Admin user processing this GRN
     * @param supplierInvoiceNo The supplier's invoice reference
     */
    public GRN(Supplier supplier, User user, String supplierInvoiceNo) {
        this.supplier = supplier;
        this.user = user;
        this.supplierInvoiceNo = supplierInvoiceNo;
        this.grnTimestamp = LocalDateTime.now();
    }

    // ==================== Getters and Setters ====================

    public Long getGrnId() {
        return grnId;
    }

    public void setGrnId(Long grnId) {
        this.grnId = grnId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public LocalDateTime getGrnTimestamp() {
        return grnTimestamp;
    }

    public void setGrnTimestamp(LocalDateTime grnTimestamp) {
        this.grnTimestamp = grnTimestamp;
    }

    public String getSupplierInvoiceNo() {
        return supplierInvoiceNo;
    }

    public void setSupplierInvoiceNo(String supplierInvoiceNo) {
        this.supplierInvoiceNo = supplierInvoiceNo;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GRNItem> getGrnItems() {
        return grnItems;
    }

    public void setGrnItems(List<GRNItem> grnItems) {
        this.grnItems = grnItems;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Adds a GRN item to this GRN.
     * Maintains bidirectional relationship.
     * Recalculates total cost.
     *
     * @param grnItem The GRN item to add
     */
    public void addGrnItem(GRNItem grnItem) {
        grnItems.add(grnItem);
        grnItem.setGrn(this);
        recalculateTotalCost();
    }

    /**
     * Removes a GRN item from this GRN.
     * Maintains bidirectional relationship.
     * Recalculates total cost.
     *
     * @param grnItem The GRN item to remove
     */
    public void removeGrnItem(GRNItem grnItem) {
        grnItems.remove(grnItem);
        grnItem.setGrn(null);
        recalculateTotalCost();
    }

    /**
     * Recalculates the total cost of this GRN.
     * Total = sum of (quantityReceived * costPrice) for all items.
     * As per FR-GRN-03.
     */
    public void recalculateTotalCost() {
        this.totalCost = grnItems.stream()
            .map(item -> item.getCostPrice()
                .multiply(BigDecimal.valueOf(item.getQuantityReceived())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Checks if this GRN is in PENDING status.
     *
     * @return true if status is PENDING, false otherwise
     */
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    /**
     * Checks if this GRN is CONFIRMED (locked).
     *
     * @return true if status is CONFIRMED, false otherwise
     */
    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(this.status);
    }

    /**
     * Confirms the GRN (marks as CONFIRMED and locks it).
     * As per FR-GRN-05: Confirmed GRNs cannot be edited.
     *
     * This method should be called by the Service layer AFTER
     * stock quantities have been updated in a transaction.
     */
    public void confirm() {
        if (isConfirmed()) {
            throw new IllegalStateException("GRN is already confirmed and cannot be modified");
        }
        this.status = "CONFIRMED";
    }

    /**
     * Checks if this GRN can be edited.
     * Only PENDING GRNs can be edited.
     *
     * @return true if editable, false if locked
     */
    public boolean isEditable() {
        return isPending();
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GRN grn = (GRN) o;
        return Objects.equals(grnId, grn.grnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grnId);
    }

    @Override
    public String toString() {
        return "GRN{" +
                "grnId=" + grnId +
                ", supplierInvoiceNo='" + supplierInvoiceNo + '\'' +
                ", totalCost=" + totalCost +
                ", status='" + status + '\'' +
                ", timestamp=" + grnTimestamp +
                ", itemCount=" + grnItems.size() +
                '}';
    }
}

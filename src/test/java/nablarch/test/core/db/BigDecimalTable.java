package nablarch.test.core.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * BigDecimal型をPKに持つテーブル。
 */
@Entity
@Table(name = "BIG_DECIMAL_TABLE")
public class BigDecimalTable {

    public BigDecimalTable() {
    }

    public BigDecimalTable(BigDecimal decimalPkCol, BigDecimal decimalCol) {
        this.decimalPkCol = decimalPkCol;
        this.decimalCol = decimalCol;
    }

    public BigDecimalTable(BigDecimal decimalPkCol) {
        this.decimalPkCol = decimalPkCol;
    }

    @Id
    @Column(name = "DECIMAL_PK_COL", precision = 15, scale = 10, nullable = false)
    public BigDecimal decimalPkCol;

    @Column(name = "DECIMAL_COL", precision = 15, scale = 10, nullable = true)
    public BigDecimal decimalCol;
}

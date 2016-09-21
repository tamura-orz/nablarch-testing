package nablarch.test.core.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "FAMILY")
public class Family {
    
    public Family() {
    };

    public Family(String famid, Father father, Daughter daughter) {
        this.famid = famid;
        this.father = father;
        this.daughter = daughter;
    }

    @Id
    @Column(name = "FAMID", length = 1, nullable = false)
    public String famid;

    @ManyToOne
    @JoinColumn(name="PARENT", referencedColumnName="MYID")
    public Father father;

    @ManyToOne
    @JoinColumn(name="CHILD2", referencedColumnName="MYID")
    public Daughter daughter;
}

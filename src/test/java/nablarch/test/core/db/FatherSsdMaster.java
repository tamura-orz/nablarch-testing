package nablarch.test.core.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "FATHER", schema = "SSD_MASTER")
public class FatherSsdMaster {
	
    public FatherSsdMaster() {
    };

	public FatherSsdMaster(String myid, GranpaSsdMaster granpa) {
		this.myid = myid;
		this.granpa = granpa;
	}

	@Id
    @Column(name = "MYID", length = 1, nullable = false)
    public String myid;
	
	@ManyToOne
	@JoinColumn(name="MY_PARENT", referencedColumnName="MYID")
    public GranpaSsdMaster granpa;
}
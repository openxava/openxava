package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * tmr PadreFiglio 
 * 
 * @author Javier Paniza
 */
@Entity @Getter @Setter
@IdClass(SimulationDetailKey.class)
@View(members="simulation;"
 	   + "product;"
 	   + "weightPercentage"
)

@Tab(properties = "simulation.product.number, simulation.product.description, product.number, product.description, weightPercentage")
public class SimulationDetail {
	
	@Id
	@ManyToOne
	@ReferenceView(value = "NoDetails")
	@NoFrame
	private Simulation simulation;
	
	@Id
	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("Simple")
	private Product product;
	
	@ReadOnly
	@Column(precision = 5, scale = 2)
	private BigDecimal weightPercentage;
	
	public BigDecimal calculateProfit(BigDecimal sellingPrice) {
		/* tmr
		if(weightPercentage == null || sellingPrice == null) {
			return BigDecimal.ZERO; 
		} else {
			return weightPercentage.multiply(sellingPrice);
		}
		*/
		// tmr ini
		if(sellingPrice == null) {
			return BigDecimal.ZERO; 
		} else {
			return new BigDecimal(2).multiply(sellingPrice);
		}		
		// tmr fin
	}

		
	/* tmr
	@Hidden
	@Column(precision = 2, scale = 0)
    private BigDecimal ordineFiglio;
	
	public BigDecimal calcolaRicavoFiglio(BigDecimal prezzoVendita) {
		if(getPercentualePeso() == null || prezzoVendita == null) {
			return BigDecimal.ZERO; 
		} else {
			return getPercentualePeso().multiply(prezzoVendita);
		}
	}
	
	public static Collection findDistinctPadreByListaFigliAndNotPadre(Collection<Articolo> listaFigli, Simulation padre)
	{
		Query query = XPersistence.getManager().createQuery("SELECT DISTINCT tb.padre FROM PadreFiglio tb WHERE tb.padre IN (SELECT e.padre "
                + "FROM PadreFiglio e "
                + "WHERE e.figlio IN (:listaFigli) AND e.padre != :padre "
                + "ORDER BY e.padre.padre.ug.ug, e.padre.padre.raggruppamento.raggruppamento, e.padre.padre.famiglia.famiglia)");
		query.setParameter("listaFigli", listaFigli);
		query.setParameter("padre", padre);
		return query.getResultList();
	}
	*/
	
}
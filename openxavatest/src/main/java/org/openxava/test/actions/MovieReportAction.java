package org.openxava.test.actions;

import java.util.*;

import javax.persistence.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * To print a datasheet of Movie concatenating simple reports. <p>
 * 
 * @author Jeromy Altuna
 */
public class MovieReportAction extends JasperConcatReportBaseAction {
	
	private Movie movie;	
	private JRDataSource[] dataSources;
	
	protected JRDataSource[] getDataSources() throws Exception {
		if(dataSources == null) {
			List images = getImages(getMovie().getPhotographs());
			dataSources = new JRDataSource[] { 	
				new JRBeanCollectionDataSource(Collections.singleton(images.get(0))),
				new JRBeanCollectionDataSource(Collections.singleton(null)),
				new JRBeanCollectionDataSource(removeFirstLast(images))
			};			
		}
		return dataSources;
	}
	
	private List removeFirstLast(List list) {
		if (list.isEmpty()) return list;
		list.remove(0);
		if (list.isEmpty()) return list;
		list.remove(list.size() - 1);
		return list;
	}

	protected String[] getJRXMLs() throws Exception {
		return "Images.jrxml:Film.jrxml:Images.jrxml".split(":");
	}
	
	protected Map getParameters(int i) throws Exception {
		switch (i) {
			case 1:
				return Maps.toMap(
					"title", 	getMovie().getTitle(), 
					"director",	getMovie().getDirector(),
					"writers",	getMovie().getWriters(),
					"starring",	getMovie().getStarring()
				);
		}
		return null;
	}
	
	private Movie getMovie() {		
		if (movie == null) {
			movie = Movie.findById(getView().getValueString("id"));
		}
		return movie;
	}
	
	private List getImages(String galleryImage) {
		Query query = XPersistence.getManager().createQuery("from GalleryImage where galleryOid=:galleryOid");
		query.setParameter("galleryOid", galleryImage);
		return query.getResultList();
	}

}

package org.openxava.actions;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * Base class for creating actions to be used as list actions.<p>
 * 
 * That is in &lt;list-action/&gt; of &lt;collection-view/&gt; or
 * in @ListAction annotation.<br>
 * 
 * @author Javier Paniza
 */

abstract public class CollectionBaseAction extends CollectionElementViewBaseAction {

	private static Log log = LogFactory.getLog(CollectionBaseAction.class);

	private List mapValues = null;
	private List<Map> mapsSelectedValues; 
	private Map [] selectedKeys; 
	private List objects;
	private List selectedObjects;
	private int row = -1;  
	

	/**
	 * A list of all collection element when each element is a map 
	 * with the values of the collection element.<p>
	 * 
	 * The values only include the displayed data in the row.<br>
	 * @return  Of type <tt>Map</tt>. Never null.
	 */
	protected List<Map> getMapValues() throws XavaException { 
		if (mapValues == null) {
			mapValues = getCollectionElementView().getCollectionValues();
		}
		return mapValues;
	}
	
	/**
	 * A list of selected collection element when each element is a map 
	 * with the values of the collection element.<p>
	 * 
	 * If <code>row</code> property has value it returns the resulting list
	 * will contain the value of that row only.<br>
	 * 
	 * The values only include the displayed data in the row.<br>
	 * 
	 * @return  Of type <tt>Map</tt>. Never null.
	 */ 
	protected List<Map> getMapsSelectedValues() throws XavaException {
		if (mapsSelectedValues == null) {
			if (row >= 0) {
				mapsSelectedValues = Collections.singletonList(getMapValues().get(row)); 	
			}			
			else {
				mapsSelectedValues = getCollectionElementView().getCollectionSelectedValues();
			}
		}
		return mapsSelectedValues;
	}

	/**
	 * Keys of the selected collection element.<p>
	 * 
	 * If <code>row</code> property has value it returns the resulting list
	 * will contain the value of that row only.<br>
	 * 
	 * The result is a Map with the key of each selected row, with the exception 
	 * of @ElementCollection that returns all the displayed values of each selected row.<br>
	 * 
	 * @return  Never null.
	 * @since 6.0.2
	 */ 	
	protected Map [] getSelectedKeys() throws XavaException {  
		if (selectedKeys == null) {
			if (row >= 0) {
				Map key;
				try {
					if (getCollectionElementView().isCollectionFromModel()) {
						key = (Map) getCollectionElementView().getCollectionValues().get(row);
						if (!getCollectionElementView().isRepresentsElementCollection()) {
							key = getCollectionElementView().getMetaModel().extractKeyValues(key); 
						}
					}
					else {
						key = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(getRow());
					}				
				} 
				catch (Exception ex) {
					log.error(XavaResources.getString("get_row_object_error", row), ex);
					throw new XavaException("get_row_object_error", row); 
				}
				 
				selectedKeys = new Map[] { key };
			}			
			else {
				
				if (getCollectionElementView().isCollectionFromModel()) {
					List<Map> selectedValues = getCollectionElementView().getCollectionSelectedValues();
					selectedKeys = new HashMap[selectedValues.size()];
					MetaModel metaModel = getCollectionElementView().getMetaModel();
					boolean elementCollection = getCollectionElementView().isRepresentsElementCollection();
					int i = 0;
					for (Map values: selectedValues) {
						selectedKeys[i++] = elementCollection?values:metaModel.extractKeyValues(values); 
					}
				}
				else {
					selectedKeys = getCollectionElementView().getCollectionTab().getSelectedKeys();
				}				

				
			}
		}
		return selectedKeys;
	}
	
	
	/**
	 * A list of all objects (POJOs or EntityBeans) in the collection.<p>
	 * 
	 * Generally the objects are POJOs, although if you use EJBPersistenceProvider
	 * the they will be EntityBeans (of EJB2).<br> 
	 *  
	 * @return  Never null.
	 */	
	protected List getObjects() throws RemoteException, FinderException, XavaException {
		if (objects == null) {
			objects = getCollectionElementView().getCollectionObjects(); 
		}
		return objects;
	}
	
	/**
	 * A list of selected objects (POJOs or EntityBeans) in the collection.<p>
	 * 
	 * Generally the objects are POJOs, although if you use EJBPersistenceProvider
	 * the they will be EntityBeans (of EJB2).<br>
	 * 
	 * If <code>row</code> property has value it returns the resulting list
	 * will contain the object of that row only.<br>
	 *  
	 * @return  Never null.
	 */	
	protected List getSelectedObjects() throws RemoteException, FinderException, XavaException {
		if (selectedObjects == null) {
			if (row >= 0) {
				try {
					if (getCollectionElementView().isCollectionFromModel()) {
						Object collectionElement = getCollectionElementView().getCollectionObjects().get(getRow());
						selectedObjects = Collections.singletonList(collectionElement);						  
					}
					else {
						Map key = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
						Object collectionElement = MapFacade.findEntity(getCollectionElementView().getModelName(), key);
						selectedObjects = Collections.singletonList(collectionElement);												
					}
									 
				} 
				catch (Exception ex) {
					log.error(XavaResources.getString("get_row_object_error", row), ex);
					throw new XavaException("get_row_object_error", row); 
				}
			}			
			else {
				selectedObjects = getCollectionElementView().getCollectionSelectedObjects();							
			}
		}
		return selectedObjects;		
	}

	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * This property has value when the action has been clicked from the row. <p>
	 * 
	 * If not its value is -1.
	 */	
	public int getRow() {
		return row;
	}
	
}

package org.openxava.tab.impl;

import java.rmi.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.converters.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * An <code>ITabProvider</code> that obtain data via JDBC. <p>
 *
 * @author  Javier Paniza
 */

public class JDBCTabProvider extends TabProviderBase {
	
	private static Log log = LogFactory.getLog(JDBCTabProvider.class);
	private Collection<TabConverter> converters;

	protected String translateCondition(String condition) { 
		return getMetaModel().getMapping().changePropertiesByColumns(condition);
	}
	
	public String toQueryField(String propertyName) {		
		return getMetaModel().getMapping().getQualifiedColumn(propertyName);
	}

	public String getSelectBase() {
		return getMetaModel().getMapping().changePropertiesByColumns(getSelectWithTableJoinsAndHiddenFields());
	}
	
	private String getSelectWithTableJoinsAndHiddenFields() {
		String select = getMetaTab().getSelect();
		int i = select.indexOf("from ${");
		if (i < 0) return select; // It's baseCondition with the complete custom SELECT 
		int f = select.indexOf("}", i);
		StringBuffer tableJoinsAndHiddenFields = new StringBuffer();
		
		Iterator itCmpFieldsColumnsInMultipleProperties = getMetaTab().getCmpFieldsColumnsInMultipleProperties()
				.iterator();
		while (itCmpFieldsColumnsInMultipleProperties.hasNext()) {
			tableJoinsAndHiddenFields.append(", ");
			tableJoinsAndHiddenFields.append(itCmpFieldsColumnsInMultipleProperties.next());
		}
		
		tableJoinsAndHiddenFields.append(" from ");
		tableJoinsAndHiddenFields.append(getMetaModel().getMapping().getTable());
		
		if (hasReferences()) {
			// the tables

			Iterator itReferencesMappings = getEntityReferencesMappings().iterator();			
			while (itReferencesMappings.hasNext()) {
				ReferenceMapping referenceMapping = (ReferenceMapping) itReferencesMappings.next();				
				tableJoinsAndHiddenFields.append(" left join ");						
				tableJoinsAndHiddenFields.append(referenceMapping.getReferencedTable());
				// select.append(" as "); // it does not work in Oracle
				tableJoinsAndHiddenFields.append(" T_");				
				String reference = referenceMapping.getReference();
				int idx = reference.lastIndexOf('_'); 
				if (idx >= 0) {
					// In the case of reference to entity in aggregate only we will take the last reference name
					reference = reference.substring(idx + 1);
				}
				String nestedReference = (String) getEntityReferencesReferenceNames().get(referenceMapping);  
				if (!Is.emptyString(nestedReference)) {
					tableJoinsAndHiddenFields.append(nestedReference);
					tableJoinsAndHiddenFields.append('_');
				}
				tableJoinsAndHiddenFields.append(reference);
				// where of join
				tableJoinsAndHiddenFields.append(" on ");
				Iterator itDetails = referenceMapping.getDetails().iterator();
				while (itDetails.hasNext()) {
					ReferenceMappingDetail detail = (ReferenceMappingDetail) itDetails.next();
					String modelThatContainsReference = detail.getContainer().getContainer().getModelName();
					if (modelThatContainsReference.equals(getMetaModel().getName())) {
						tableJoinsAndHiddenFields.append(detail.getQualifiedColumn());
					}
					else {
						tableJoinsAndHiddenFields.append("T_");												
						tableJoinsAndHiddenFields.append(nestedReference); 
						tableJoinsAndHiddenFields.append(".");
						tableJoinsAndHiddenFields.append(detail.getColumn());												
					}
					tableJoinsAndHiddenFields.append(" = ");
					tableJoinsAndHiddenFields.append("T_");
					if (!Is.emptyString(nestedReference)) {
						tableJoinsAndHiddenFields.append(nestedReference);
						tableJoinsAndHiddenFields.append('_');
					}
					tableJoinsAndHiddenFields.append(reference); 
					tableJoinsAndHiddenFields.append(".");
					tableJoinsAndHiddenFields.append(detail.getReferencedTableColumn());					
					
					if (itDetails.hasNext()) {
						tableJoinsAndHiddenFields.append(" and ");
					}
				}
			}
		}
		
		resetEntityReferencesMappings();
		
		StringBuffer result = new StringBuffer(select);
		result.replace(i, f + 2, tableJoinsAndHiddenFields.toString());
		return result.toString();
	}
	

	public DataChunk nextChunk() throws RemoteException {		
		if (getSelect() == null || isEOF()) { // search not called yet
			return new DataChunk(Collections.EMPTY_LIST, true, getCurrent()); // Empty
		}
		ResultSet resultSet = null;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnectionProvider().getConnection();
			ps = createPreparedStatement(con);
			resultSet = nextBlock(ps);
			List data = new ArrayList();
			int f = 0;
			int nc = resultSet == null?0:resultSet.getMetaData().getColumnCount(); 
			while (resultSet != null && resultSet.next()) {
				if (++f > getChunkSize()) {
					setCurrent(getCurrent() + getChunkSize());
					return new DataChunk(data, false, getCurrent());
				}
				Object[] row = new Object[nc];
				for (int i = 0; i < nc; i++) {					
					row[i] = resultSet.getObject(i + 1);
				}
				data.add(row);
			}
			// No more
			setEOF(true);
			return new DataChunk(data, true, getCurrent());
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("select_error", getSelect()), ex);
			throw new RemoteException(XavaResources.getString("select_error", getSelect()));
		}
		finally {
			try {
				if (resultSet != null) resultSet.close();
				if (ps != null) {
					ps.close();
					ps = null;
				}
			}
			catch (Exception ex) {
			}
			try {
				con.close();
			}
			catch (Exception ex) {
			}
		}
	}
		
	/**
	 * Creates a <code>ResultSet</code> with the next block data. <p>
	 */
	private ResultSet nextBlock(PreparedStatement ps) throws SQLException {
		if (ps == null) return null;

		// Fill key values
		StringBuffer message =
			new StringBuffer("[JDBCTabProvider.nextBlock] ");
		message.append(XavaResources.getString("executing_select", getSelect()));		
		
		Object [] key = getKey();
		for (int i = 0; i < key.length; i++) {
			ps.setObject(i + 1, key[i]);
			message.append(key[i]);
			if (i < key.length - 1)
				message.append(", ");
		}
		log.debug(message);
		
		if ((getCurrent() + getChunkSize()) < Integer.MAX_VALUE) { 
			ps.setMaxRows(getCurrent() + getChunkSize() + 1); 
		}		
		ResultSet rs = ps.executeQuery();						
		position(rs);

		return rs;
	}

	private PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		/* Not in this way because TYPE_SCROLL_INTENSIVE has a very poor performance
		   in some databases
		  PreparedStatement ps =
			con.prepareStatement(
				select,
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		*/
		
		if (keyHasNulls()) return null; // Because some databases (like Informix) have problems setting nulls
				
		return con.prepareStatement(getSelect());
	}
	
	/**
	 * Position the <code>ResultSet</code> in the appropiate part. <p>
	 *
	 * @param rs  <tt>!= null</tt>
	 */
	private void position(ResultSet rs) throws SQLException { 
		//rs.absolute(current); // this only run with TYPE_SCROLL_INSENSITIVE, and this is very slow on executing query in some databases
		for (int i = 0; i < getCurrent(); i++) {
			if (!rs.next())
				return;
		}
	}


	protected Number executeNumberSelect(String select, String errorId) {
		if (select == null) return Integer.MAX_VALUE; 
		if (keyHasNulls()) return 0;						
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = getConnectionProvider().getConnection();
			ps = con.prepareStatement(select);
			Object [] key = getKey();
			for (int i = 0; i < key.length; i++) {
				ps.setObject(i + 1, key[i]);				
			}			
			rs = ps.executeQuery();
			rs.next();
			return (Number) rs.getObject(1);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException(errorId);
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				log.error(XavaResources.getString("close_resultset_warning"), ex);
			}			
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ex) {
				log.error(XavaResources.getString("close_statement_warning"), ex);
			}
			try {
				con.close();
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("close_connection_warning"), ex);
			}
		}						
	}
	
	private IConnectionProvider getConnectionProvider() {
		return DataSourceConnectionProvider.getByComponent(getMetaModel().getMetaComponent().getName()); 
	}
	
	public Collection<TabConverter> getConverters() throws XavaException {
		if (converters == null) {
			converters = new ArrayList();			
			int i=0;
			String table = getMetaModel().getMapping().getTableToQualifyColumn(); 
			for (String propertyName: getMetaTab().getPropertiesNamesWithKeyAndHidden()) {
				try {
					MetaProperty property = getMetaModel().getMetaProperty(propertyName);
					PropertyMapping propertyMapping = property.getMapping();
					if (propertyMapping != null) {
						IConverter converter = propertyMapping.getConverter();
						if (converter != null) {
							converters.add(new TabConverter(propertyName, i,  converter));
						}
						else {							
							IMultipleConverter multipleConverter =  propertyMapping.getMultipleConverter();
							if (multipleConverter != null) {							
								converters.add(new TabConverter(propertyName, i, multipleConverter, propertyMapping.getCmpFields(), getFields(), table));
							}
							else {
								// This is the case of a key without converter of type int or long
								// It's for suporting int and long as key and NUMERIC in database
								// without to declare an explicit converter
								if (property.isKey()) {
									if (property.getType().equals(int.class) || property.getType().equals(Integer.class)) {
										converters.add(new TabConverter(propertyName, i,  IntegerNumberConverter.getInstance()));
									}
									else if (property.getType().equals(long.class) || property.getType().equals(Long.class)) {
										converters.add(new TabConverter(propertyName, i,  LongNumberConverter.getInstance()));
									}
								}
							}	
						}
					}
				}
				catch (ElementNotFoundException ex) {
					// Thus we exclude the property out of mapping
				}
				i++;
			}
		}
		return converters;
	}
	
	private String[] getFields() throws XavaException {
		Collection c = new ArrayList();
		// First the key
		Iterator itKeyNames = getMetaModel().getAllKeyPropertiesNames().iterator();
		while (itKeyNames.hasNext()) {
			c.add(getMetaModel().getMapping().getQualifiedColumn((String) itKeyNames.next()));
		}
				
		// Then the others
		c.addAll(getMetaTab().getTableColumns());
		c.addAll(getMetaTab().getHiddenTableColumns());
				
		String[] result = new String[c.size()];
		c.toArray(result);		
		return result;
	}

	public boolean usesConverters() {
		return true;
	}

	protected String translateProperty(String property) {
		return getMetaModel().getMapping().getQualifiedColumn(property);
	}
	
	protected String noValueInSelect() { 
		return "null";
	}

	/** @since 6.2.1 */
	protected void addEntityReferenceMapping(Collection<ReferenceMapping> entityReferencesMappings,
		Map<ReferenceMapping, String> entityReferencesReferenceNames, ReferenceMapping referenceMapping, String parentReference) 
	{
		if (!entityReferencesMappings.contains(referenceMapping)) {
			entityReferencesReferenceNames.put(referenceMapping, parentReference); 
			entityReferencesMappings.add(referenceMapping);
		}

	}

	protected String toSearchByCollectionMemberSelect(String select) { 
		// Searching by collection members is not supported with JDBC yet
		return select;
	}

}

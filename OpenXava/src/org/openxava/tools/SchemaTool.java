package org.openxava.tools;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.metamodel.*;

import org.apache.commons.io.*;
import org.apache.commons.logging.*;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.*;
import org.hibernate.internal.*;
import org.hibernate.service.*;
import org.hibernate.tool.hbm2ddl.*;
import org.hibernate.tool.schema.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 *
 * @since 5.3
 * @author Javier Paniza 
 */

public class SchemaTool {
	
	private static Log log = LogFactory.getLog(SchemaTool.class);	
	private boolean commitOnFinish = true;
	private boolean excludeEntitiesWithExplicitSchema = false; 
	private Collection<Class> annotatedClasses = null;
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0 || Is.emptyString(args[0])) {
			log.error(XavaResources.getString("schematool_action_required")); 
			return;
		}				
		if (args.length == 1 || Is.emptyString(args[1])) {
			log.error(XavaResources.getString("schematool_persistence_unit_required")); 
			return;
		}	
		XPersistence.setPersistenceUnit(args[1]);
		SchemaTool tool = new SchemaTool();
		String action = args[0];
    	if (action.equals("update")) {
    		tool.updateSchema();
    	}
    	else if (action.equals("generate")) {
    		tool.printSchema();
    	}
    	else {
   			log.error(XavaResources.getString("schematool_action_required"));     		
    	}		
    	System.exit(0); // To avoid Eclipse hangs when executing Ant task
	}
	
	public void updateSchema() {
		execute(true, false);
	}
	
	public void generateSchema() {
		execute(false, false);
	}
	
	public void printSchema() {
		execute(false, true);
	}
	
	private void execute(boolean update, boolean console) { 
		try {
			Map<String, Object> factoryProperties = XPersistence.getManager().getEntityManagerFactory().getProperties();
			StandardServiceRegistryBuilder serviceRegistryBuilder =	new StandardServiceRegistryBuilder();
			String [] properties = {
				"hibernate.connection.driver_class", 	
				"hibernate.dialect", 	
				"hibernate.connection.url", 
				"hibernate.default_schema", 
				"hibernate.connection.datasource"
			};
			for (String property: properties) {
				Object value = factoryProperties.get(property);
				if (value != null) {
					serviceRegistryBuilder.applySetting(property, value);
				}
			}
			ServiceRegistry serviceRegistry	= serviceRegistryBuilder.build();
			MetadataSources metadata = new MetadataSources(serviceRegistry);
			
			if (annotatedClasses != null) {
				for (Class annotatedClass: annotatedClasses) {
					metadata.addAnnotatedClass(annotatedClass);
				}						
			}
			else {
				metadata.addResource("GalleryImage.hbm.xml");
				for (ManagedType type: XPersistence.getManager().getMetamodel().getManagedTypes()) {
					Class<?> clazz = type.getJavaType();
					if (clazz == null || clazz.isInterface()) continue;
					metadata.addAnnotatedClass(clazz);
				}		
			}
	
			String fileName = Files.getOpenXavaBaseDir() + "ddl-" + UUID.randomUUID() + ".sql";
			File file = new File(fileName);
			String schema = (String) factoryProperties.get("hibernate.default_schema");
			java.sql.Connection connection = ((SessionImpl) XPersistence.getManager().getDelegate()).connection(); 
	    	boolean supportsSchemasInIndexDefinitions = supportsSchemasInIndexDefinitions(connection); 
	    	XPersistence.commit();			
	    	if (update) {
				SchemaUpdate schemaUpdate = new SchemaUpdate();
				schemaUpdate.setOutputFile(fileName);
				schemaUpdate.execute(EnumSet.of(TargetType.SCRIPT), metadata.buildMetadata());
				Collection<String> scripts = FileUtils.readLines(file);
		    	for (String script: scripts) {
		    		if (excludeEntitiesWithExplicitSchema && script.contains(".")) continue; 
		    		String scriptWithSchema = addSchema(schema, script, supportsSchemasInIndexDefinitions);
		    		log.info(XavaResources.getString("executing") + ": " + scriptWithSchema);
		    		try {
						Query query = XPersistence.getManager().createNativeQuery(scriptWithSchema);
						query.executeUpdate();
						XPersistence.commit();
		    		}
		    		catch (Exception ex) {
		    			// In this case Hibernate logs the cause
		    			XPersistence.rollback();
		    		}
		    	}
		    	
	    	}
	    	else {
				SchemaExport schemaExport = new SchemaExport();
				schemaExport.setOutputFile(fileName);
				schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata.buildMetadata());
				Collection<String> scripts = FileUtils.readLines(file);
				for (String script: scripts) {
					if (excludeEntitiesWithExplicitSchema && script.contains(".")) continue; 
					String scriptWithSchema = addSchema(schema, script, supportsSchemasInIndexDefinitions); 
					if (console) {
						System.out.print(scriptWithSchema); 
						System.out.println(';');
					}
					else {
						log.info(XavaResources.getString("executing") + ": " + scriptWithSchema);
						Query query = XPersistence.getManager().createNativeQuery(scriptWithSchema);
						query.executeUpdate();
					}
				}
	    	}
	    	FileUtils.deleteQuietly(file);
		}
		catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
		finally {
			if (commitOnFinish) XPersistence.commit();
		}

	}
	
	private boolean supportsSchemasInIndexDefinitions(Connection connection) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		if ("PostgreSQL".equals(metaData.getDatabaseProductName())) return false;
		return metaData.supportsSchemasInIndexDefinitions();
	}

	private static String addSchema(String schema, String script, boolean supportsSchemasInIndexDefinitions) {
		if (Is.emptyString(schema)) return script;
		if (script.contains(".")) return script; 
		script = script.replaceAll("create table ", "create table " + schema + "."); 
		script = script.replaceAll("alter table ", "alter table " + schema + "."); 
		script = script.replaceAll("\\) references ", ") references " + schema + ".");
		script = script.replaceAll("create sequence ", "create sequence " + schema + ".");
		script = script.replaceAll("create index ", "create index " + schema + (supportsSchemasInIndexDefinitions?".":"_")); 
		script = script.replaceAll(" on ", " on " + schema + ".");
		return script;
	}

	public boolean isCommitOnFinish() {
		return commitOnFinish;
	}

	public void setCommitOnFinish(boolean commitOnFinish) {
		this.commitOnFinish = commitOnFinish;
	}

	public void addAnnotatedClass(Class annotatedClass) {
		if (annotatedClasses == null) annotatedClasses = new ArrayList<Class>(); 
		annotatedClasses.add(annotatedClass);		
	}
	
	/** @since 5.7 */
	public boolean isExcludeEntitiesWithExplicitSchema() {
		return excludeEntitiesWithExplicitSchema;
	}

	/** @since 5.7 */
	public void setExcludeEntitiesWithExplicitSchema(boolean excludeEntitiesWithExplicitSchema) {
		this.excludeEntitiesWithExplicitSchema = excludeEntitiesWithExplicitSchema;
	}

}

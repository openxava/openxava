
import org.openxava.generators.*;
import org.openxava.application.meta.*;
import org.openxava.converters.StringBlobConverter.*;

import java.io.*;
import java.util.*;

import org.openxava.util.*;

/**
 * @author Javier Paniza, Alexandr Vladimirov
 */
public class PortletCodeGenerator {
	
	private Locale defaultLocale = Locale.getDefault();
		
	private String project;
	private String pagesDir;
	private boolean generateJetspeed2Files;
	private String encoding;
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public String getPagesDir() {
		return pagesDir;
	}

	public void setPagesDir(String pagesDir) {
		this.pagesDir = pagesDir;
	}
	
	public boolean isGenerateJetspeed2Files() {
		return generateJetspeed2Files;
	}

	public void setGenerateJetspeed2Files(boolean generateJetspeed2Files) {
		this.generateJetspeed2Files = generateJetspeed2Files;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getEncoding() {
		return encoding;
	}	
	
	public void setDefaultLocale(String locale) {
		if (!Is.emptyString(locale)) {
			defaultLocale = new Locale(locale);
		}
	}
			
	private void run() throws Exception {		 
		try {
			Locale.setDefault(defaultLocale);
			
			// portlet.xml
			String [] argvPortlet = {
				"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used
				"../" + project + "/web/WEB-INF/portlet.xml",
				project, 
				encoding
			};
			PortletXmlPG.main(argvPortlet);
			
			// liferay-display.xml
			String [] argvLiferayDisplay = {
				"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used
				"../" + project + "/web/WEB-INF/liferay-display.xml",
				project, 
				encoding
			};
			LiferayDisplayXmlPG.main(argvLiferayDisplay);
						
			if (isGenerateJetspeed2Files()) {				
				
				// Remove all
				File projectFolder = new File(pagesDir + "/" + project);
				Files.deleteDir(projectFolder);
				projectFolder.mkdirs();

				// Obtaining application 
				MetaApplication app = MetaApplications.getMetaApplication(project);				
				
				// folder.metadata				
				String [] argvFolder = {				
					"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used				
					pagesDir + "/" + project + "/folder.metadata",
					project
				};			
				Jetspeed2FolderPG.main(argvFolder);
				
				for (Iterator it=app.getFolders().iterator(); it.hasNext();) {
					String folder = (String) it.next();
					String folderURL = pagesDir + "/" + project + "/" + folder;
					new File(folderURL).mkdirs();
					String [] argvSubFolder = {				
						"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used				
						folderURL + "/folder.metadata",
						project,
						folder
					};
					Jetspeed2FolderPG.main(argvSubFolder);
				}
				
				// ds			
				/* Automatic Document Set generates a too much complicate UI
				String [] argvDs = {				
					"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used				
					pagesDir + "/" + project + "/" + project + ".ds",
					project
				};
				Jetspeed2DsPG.main(argvDs);
				
				for (Iterator it=app.getFolders().iterator(); it.hasNext();) {
					String folder = (String) it.next();	
					if (Is.emptyString(folder)) continue;					
					String [] argvSubDs = {				
						"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used				
						pagesDir + "/" + project + "/" + folder + "/"  + Strings.lastToken(folder, "./") + ".ds",
						project,
						folder
					};
					Jetspeed2DsPG.main(argvSubDs);
				}
				*/
								
				// psml							
				for (Iterator it=app.getMetaModules().iterator(); it.hasNext();) {
					MetaModule module = (MetaModule) it.next();
					String moduleFolder = Is.emptyString(module.getFolder())?"":module.getFolder() + "/";
					String folder = pagesDir + "/" + project + "/" + moduleFolder;					
					String [] argvPsml = {				
						"../OpenXava/xava/default-size.xml", // some valid xml is required by TL, not used				
						folder + module.getName() + ".psml", 
						project,
						module.getName(),
						module.getFolder()
					};
					Jetspeed2PsmlPG.main(argvPsml);
				}				
				
			}
			
			// i18n resource files
			File f = new File("../" + project + "/i18n/portlets/");
			f.mkdir();				
			MetaApplication app = MetaApplications.getMetaApplication(project);
			for (Iterator it=app.getMetaModules().iterator(); it.hasNext();) {
				MetaModule module = (MetaModule) it.next();
				createI18nFiles(app, module);			
			}
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(XavaResources.getString("generation_project_code_error", project)); 
		}
	}	
	
	private void createI18nFiles(MetaApplication app, MetaModule module) throws Exception {
		Collection locales = Generators.getAvailableLocales("../" + project + "/i18n");
		for (Object olocale: locales) {
			Locale locale = (Locale) olocale;
			Properties i18n = new Properties();			
			i18n.put("category." + app.getId(), app.getLabel(locale));
			String description = module.getDescription(locale);
			if (Is.empty(description)) description = module.getLabel(locale);
			i18n.put("javax.portlet.title", app.getLabel(locale) + " - " +  description);
			i18n.put("javax.portlet.short-title", module.getLabel(locale));
			i18n.store(new FileOutputStream("../" + project + "/i18n/portlets/" + module.getName() + "_" + locale.getLanguage() + ".properties"), null);			
		}
	}
	
	public static void main(String [] argv) {
		if (argv.length < 2 || argv.length > 5) {
			System.err.println(XavaResources.getString("jetspeed2_generator_argv_required")); 
			System.exit(1);			
		}
		try {		
			PortletCodeGenerator g = new PortletCodeGenerator();			
			g.setProject(argv[0]);			
			g.setPagesDir(argv[1]);
			g.setGenerateJetspeed2Files(Boolean.valueOf(argv[2]).booleanValue()); // optional
			g.setEncoding(argv[3]);
			if (argv.length > 4) {
				g.setDefaultLocale(argv[4]);
			}
			g.run();			
			System.exit(0); 
		}	
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(2);
		}	
	}
	
}

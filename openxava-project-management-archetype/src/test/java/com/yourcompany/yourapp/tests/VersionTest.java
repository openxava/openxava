package com.yourcompany.yourapp.tests;

import org.openxava.jpa.*;
import org.openxava.tests.*;

import com.yourcompany.yourapp.model.*;

public class VersionTest extends ModuleTestBase {

	public VersionTest(String nameTest) {
		super(nameTest, "xavaprojects", "Version");
	}
	
	public void testVersionsDependsOnProjectInIssue_projectDefaultValueOnCreateIssueFromVersion() throws Exception {
		login("admin", "admin");
		execute("CRUD.new");
		execute("Reference.createNew", "model=Project,keyProperty=project.id");
		setValue("name", "XavaProjects");
		execute("NewCreation.saveNew");
		setValue("name", "2019.12");
		execute("CRUD.save");

		Project newProject = Project.findByName("XavaProjects"); 
		setValue("project.id", newProject.getId());
		setValue("name", "2020.01");

		assertNoAction("Collection.new");
		execute("VersionIssues.new", "viewObject=xava_view_issues");
		assertNoEditable("project");
		assertDescriptionValue("project.id", "XavaProjects");
		closeDialog();

		execute("CRUD.save");
		
		XPersistence.commit();
		Version newVersion1 = Version.findByName("2019.12").get(0);
		Version newVersion2 = Version.findByName("2020.01").get(0);

		changeModule("Issue");
		execute("CRUD.new");
		assertValue("project.id", "");
		
		Project oldProject = Project.findByName("OpenXava");
		String [][] projects = {
			{"", ""},
			{oldProject.getId(), oldProject.getName() },
			{newProject.getId(), newProject.getName() }
		};
		
		String [][] empty = {
			{"", ""}
		};
		
		assertValidValues("project.id", projects);
		assertValidValues("version.id", empty);
		
		setValue("project.id", oldProject.getId());
		String [][] oldProjectVersions = {
			{ "", "" },
			{ "4028808d7eea19fe017eea5074130012", "2.0" },
			{ "4028808d7eea19fe017eea5057f30011", "1.0" }
		};
		assertValidValues("version.id", oldProjectVersions);
		
		setValue("project.id", newProject.getId());
		String [][] newProjectVersions = {
			{ "", "" },
			{ newVersion2.getId(), newVersion2.getName() },
			{ newVersion1.getId(), newVersion1.getName() }
			
		};
		assertValidValues("version.id", newProjectVersions);
		
		changeModule("Version");
		execute("Mode.list");
		assertListRowCount(4);
		setConditionValues(newProject.getName()); 
		assertListRowCount(2);
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
		
		newProject = XPersistence.getManager(). merge(newProject);
		XPersistence.getManager().remove(newProject);
	}	

}

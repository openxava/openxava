package org.openxava.actions;


/**
 * How to use the SimpleHtmlReportAction<br>
	<br>
	Process<br>
	Create an action which extends SimpleHtmlReportAction. <br>
	Create a report template<br>
	Add the action in controllers.xml, <br>
	<br>
	Action<br>
	Create an action which extends SimpleHtmlReportAction. <br>
	If your entity does not contain collections, you don't have to create
	this action, you can use SimpleHtmlReportAction <br>
	<pre><br>public class ReportProjectAction extends SimpleHtmlReportAction {<br><br>	public Map&lt;String, Object&gt; getParameters() throws Exception {         <br>		Project p = (Project)MapFacade.findEntity(getModelName(), getView().getKeyValuesWithValue());<br>		return getParameters(p);<br>	}<br><br>	public static Map&lt;String, Object&gt; getParameters(Project p) throws Exception {         <br>		Map&lt;String, Object&gt; parameters = new HashMap&lt;String, Object&gt;();        <br>		// get all the field contents of the entity<br>		parameters.putAll(Objects.getEntityParameters(p));<br>		// get the field contents of the collections<br>		parameters.put("milestones", Objects.getCollectionParametersList(p.getMilestones()));<br>		parameters.put("actions", Objects.getCollectionParametersList(p.getActions()));<br>		return parameters;<br>	}<br>}<br></pre>
	In most cases, you just have to call
	Objects.getEntityParameters(your_entity) and
	Objects.getCollectionParametersList(your_collection) in the action.<br>
	By defining a static function to get the parameters, you can reuse the
	code for an extension of SimpleHtmlMailBaseAction.<br>
	<br>
	Report<br>
	Create a report template in /reports (if this folder does not exist,
	create it as a source folder). You can create it with any WYSIWYG
	editor (SeaMonkey is a good free one).<br>
	This template should be called entity_name.html, but you can create
	others as you want. The (very simple) syntax for the report is
	explained at the end.<br>
	<pre>&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"&gt;<br>&lt;html xmlns="http://www.w3.org/1999/xhtml"&gt;<br>&lt;head&gt;<br>  &lt;meta http-equiv="content-type" content="text/html; charset=UTF-8"&gt;<br>  &lt;title&gt;Project - ${reference}&lt;/title&gt;<br>  &lt;link href="/OpenXavaTest/xava/style/report.css" rel="stylesheet" type="text/css"&gt;<br>&lt;/head&gt;<br>&lt;body&gt;<br>&lt;table&gt;<br>	&lt;tr&gt;&lt;td&gt;Name:&lt;/td&gt;&lt;td&gt;${name}&lt;/td&gt;&lt;/tr&gt;<br>	&lt;tr&gt;&lt;td&gt;Reference:&lt;/td&gt;&lt;td&gt;${reference}&lt;/td&gt;&lt;/tr&gt;<br>	&lt;tr&gt;&lt;td&gt;Owner:&lt;/td&gt;&lt;td&gt;${owner.firstName} ${owner.lastName}&lt;/td&gt;&lt;/tr&gt;<br>&lt;!-- $$if(customers) --&gt;<br>	&lt;tr&gt;&lt;td&gt;Customers:&lt;/td&gt;&lt;td&gt;${customer}&lt;/td&gt;&lt;/tr&gt;<br>&lt;!-- $$endif(customers) --&gt;<br>&lt;/table&gt;<br>&lt;table&gt;<br>	&lt;tr&gt;<br>		&lt;td&gt;Milestone&lt;/td&gt;<br>		&lt;td&gt;Target&lt;/td&gt;<br>		&lt;td&gt;Achieved&lt;/td&gt;<br>	&lt;/tr&gt;<br>&lt;!-- $$for(milestones) --&gt;<br>	&lt;tr&gt;<br>		&lt;td&gt;${milestone.name}&lt;/td&gt;<br>		&lt;td&gt;${targetDate}&lt;/td&gt;<br>		&lt;td&gt;${achievedDate}&lt;/td&gt;<br>	&lt;/tr&gt;<br>&lt;!-- $$endfor(milestones) --&gt;<br>&lt;/table&gt;<br>&lt;/body&gt;<br>&lt;/html&gt;<br></pre>
	controllers.xml<br>
	<pre>	&lt;controller name="Project"&gt;	<br>		&lt;extends controller="Typical"/&gt;<br>        	&lt;action name="datasheet" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.ReportProjectAction" &gt;<br>			&lt;set property="template" value="/Project.html" /&gt;<br>		&lt;/action&gt;<br>	        &lt;action name="actionsReport" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.ReportProjectAction" &gt;<br>			&lt;set property="template" value="/ProjectActions.html" /&gt;<br>		&lt;/action&gt;<br>	&lt;/controller&gt;<br><br>	&lt;controller name="Company"&gt;	<br>		&lt;extends controller="Typical"/&gt;<br>        	&lt;action name="report" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.SimpleHtmlReportAction" /&gt;<br>	&lt;/controller&gt;<br><br>	&lt;controller name="SimpleHtmlReport"&gt;	<br>        	&lt;action name="report" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.SimpleHtmlReportAction" /&gt;<br>	&lt;/controller&gt;<br><br></pre>
	Here we have used twice the same report action with 2 different
	templates for Project to report on different part of the entity.<br>
	We also have use the SimpleHtmlReportAction if we don't want to display
	collections in the report such as in the Company controller.<br>
	<br>
	<br>
	Developping reports very fast<br>
	<br>
	1. In application.xml, add the SimpleHtmlReport controller to the
	module you want to report on.<br>
	2. In /reports, create a report named entity_name.html, open it in
	Eclipse and just write ${fields} inside, save<br>
	3. Start Tomcat, launch your browser, select your module and click on
	Report, a report will be generated with all the available fields<br>
	4. Save this report under /reports/entity_name.html<br>
	5. Done! If you refresh the reports folder in Eclipse and wait a little
	bit, when you click on report in the browser you will see a complete
	report of your entity (without the collections)<br>
	<br>
	<br>
	Syntax for the template<br>
	<br>
	Field names<br>
	${field_name} is replaced in the template by the value contained in the
	Map returned by getParameters()<br>
	field_name can contain references such as ${parent.child.name} with a
	default (adjustable) depth of 5.<br>
	<br>
	Control<br>
	There are 3 types of control blocks<br>
	if<br>
	Syntax: &lt;!-- $$if(field_name) --&gt;Some text which can contain
	control blocks&lt;!-- $$endif(field_name) --&gt;<br>
	The content of the block will only appear if field_name IS in the
	getParameters() Map and is not empty.<br>
	<br>
	ifnot<br>
	Syntax: &lt;!-- $$ifnot(field_name) --&gt;Some text which can contain
	control blocks&lt;!-- $$endifnot(field_name) --&gt;<br>
	The content of the block will only appear if field_name is NOT in the
	getParameters() Map or is empty.<br>
	<br>
	for<br>
	Syntax: &lt;!-- $$for(field_name) --&gt;Some text which can contain
	control blocks&lt;!-- $$endfor(field_name) --&gt;<br>
	The content of the block will be repeated as many times as there are
	items in the Vector&lt;Map&lt;String, Object&gt;&gt; returned by
	getParameters().get(field_name)<br>
 * 
 * @author Laurent Wibaux 
 */

public class SimpleHTMLReportAction extends SimpleTemplaterAction 
implements IForwardAction { 
		
	public String getForwardURI() {
        String uri = "/xava/report.html?time=" + System.currentTimeMillis();
        return "javascript:void(window.open(openxava.contextPath + '" + uri + "'))";		
    }

    public boolean inNewWindow() {
        return false;
    }  	
	
}
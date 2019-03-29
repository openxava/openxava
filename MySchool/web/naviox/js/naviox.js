if (naviox == null) var naviox = {};

naviox.init = function() {
		
	naviox.watchSearch(); 
	
	if (naviox.locked) {
		naviox.lockUser();
	}
	else if (naviox.lockSessionMilliseconds > 0) {
		naviox.watchForIdleUser();
		openxava.postRefreshPage = naviox.watchForIdleUser;
	}
	
	$('#modules_list_core').css('height', 'calc(100vh - ' + $('#modules_list_top').height() + 'px)'); 
}

naviox.watchForIdleUser = function() {
	clearTimeout(naviox.idleWatcher);
	naviox.idleWatcher = setTimeout(naviox.lockUser, naviox.lockSessionMilliseconds);
}

naviox.lockUser = function() {
	openxava.executeAction(naviox.application, naviox.module, '', false, "SessionLocker.lock");
}

naviox.watchSearch = function() { 
	jQuery( "#search_modules_text" ).typeWatch({
		callback: naviox.filterModules,
	    wait:500,
	    highlight:true,
	    captureLength:0
	});
	
	$( "#search_modules_text" ).keyup(function() {
		if ($(this).val() == "") naviox.displayModulesList(); 
	});	
}

naviox.bookmark = function() {
	var bookmark = $('#bookmark');
	var bookmarkClass = bookmark.attr('class');
	if (naviox.changeBookmark(bookmark, bookmarkClass, "star-outline", "star")) {
		Modules.bookmarkCurrentModule();
	}
	else if (naviox.changeBookmark(bookmark, bookmarkClass, "star", "star-outline")) {
		Modules.unbookmarkCurrentModule();
	}		
}

naviox.changeBookmark = function(bookmark, bookmarkClass, from, to) {
	if (bookmarkClass == "mdi mdi-" + from) {
		bookmark.attr('class', "mdi mdi-" + to);
		return true;
	}	
	return false;
}

naviox.filterModules = function() {
	Modules.filter($("#search_modules_text").val(), naviox.refreshSearchModulesList);
}

naviox.displayModulesList = function() { 
	Modules.displayModulesList(naviox.refreshModulesList);  
}

naviox.displayAllModulesList = function(searchWord) {  
	Modules.displayAllModulesList(searchWord, naviox.refreshModulesList);  
}

naviox.goFolder = function(folderOid) {
	Folders.goFolder(folderOid, naviox.refreshFolderModulesList);
}

naviox.goBack = function(folderOid) {
	Folders.goBack(naviox.refreshFolderBackModulesList);
}

naviox.refreshModulesList = function(modulesList) { 
	if (modulesList == null) {
		window.location=openxava.location="..";
		return;
	}
	$('#modules_list_core').html(modulesList);
	$('#modules_list_header').show();
	$('#modules_list_search_header').hide();
}

naviox.refreshSearchModulesList = function(modulesList) { 
	if (modulesList == null) {
		window.location=openxava.location="..";
		return;
	}
	$('#modules_list_core').html(modulesList);
	$('#modules_list_header').hide();
	$('#modules_list_search_header').show();	
}

naviox.refreshFolderModulesList = function(modulesList) {
	if (modulesList == null) {
		window.location=openxava.location="../m/SignIn";
		return;
	}
	$('#modules_list_content').append("<td></td>"); 
	$('#modules_list_content').children().last().html(modulesList);
	
	$('.modules-list-header').width($(window).width()); 
	
	var box = $('#modules_list_box');
    box.animate({
    		left: -box.outerWidth() / 2
    	},    	
    	function() {
    		$('#modules_list_content').children().first().remove();
    		box.css("left", "0");
    		naviox.watchSearch();
    		$('.modules-list-header').css("width", "100%"); 
    	}
    );
}

naviox.refreshFolderBackModulesList = function(modulesList) {
	if (modulesList == null) {
		window.location=openxava.location="..";
		return;
	}
	$('#modules_list_content').prepend("<td></td>"); 
	var box = $('#modules_list_box');
	box.css("left", "-" + box.outerWidth() + "px");
	$('#modules_list_content').children().first().html(modulesList);

	$('.modules-list-header').width($(window).width()); 
		
    box.animate({
    		left: 0 
    	},    	
    	function() {
    		$('#modules_list_content').children().last().remove();
    		naviox.watchSearch(); 
    		$('.modules-list-header').css("width", "100%"); 
    	}
    );    
}

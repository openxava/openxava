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
	
	// tmr naviox.initModulesLoading();
	// tmr ini 
	naviox.initModulesList();
	naviox.initBookmark();
	// tmr fin 
}

// tmr naviox.initModulesLoading = function() {
naviox.initModulesList = function() { // tmr
	$('#modules_list_core .module-row').on( "click", function() {
  		$(this).find(".module-loading").show();
	});
	/* tmr
	$('#more_modules').on( "click", function() {
		console.log("[naviox.initModulesList] #more_modules"); // tmr
		$('#loading_more_modules').show(); 
		$('#load_more_modules').hide();
	});
	*/
	// tmr ini
	$('#modules_list_hide').on( "click", function() {
		naviox.hideModulesList(naviox.application, naviox.module);
	});
	$('#modules_list_show, #module_header_menu_button').on( "click", function() {
		naviox.showModulesList(naviox.application, naviox.module);
	});		
	$('#display_all_modules').on( "click", function() {
		console.log("[naviox.initModulesList] #display_all_modules"); // tmr
		naviox.displayAllModulesList($(this).data("search-word"));
	});
	// tmr fin
}

naviox.initBookmark = function() { // tmr
	$('#bookmark').on( "click", function() {
		naviox.bookmark();
	});
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
	var bookmark = $('#bookmark').children(":first"); 
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

naviox.hideModulesList = function(application, module) {
	$('#modules_list_hide').hide();
	$('#module_header_menu_button').show();
	$('#module_extended_title').show();
	$('#modules_list').animate({width:'toggle'}, 200, function() {
		openxava.resetListsSize(application, module);
	});
}

naviox.showModulesList = function(application, module) {
	$('#module_header_menu_button').hide();
	$('#module_extended_title').hide();
	$('#modules_list').animate({width:'toggle'}, 200, function() {
		$('#modules_list_hide').fadeIn(); 
		openxava.resetListsSize(application, module); 
	});
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
    openxava.initInlineEvents(); 
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
    openxava.initInlineEvents();     
}

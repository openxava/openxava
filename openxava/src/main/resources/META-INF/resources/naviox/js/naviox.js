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
	
	naviox.initLeftMenu(); 
	naviox.initModulesList();
	naviox.initBookmark();
	naviox.initModuleHeader();
	if (typeof chat !== 'undefined') {
		chat.initPanel();
	}
}

naviox.initLeftMenu = function() { 
	$('#modules_list_hide').on( "click", function() {
		naviox.hideModulesList(naviox.application, naviox.module);
	});
	$('#modules_list_show, #module_header_menu_button').on( "click", function() {
		naviox.showModulesList(naviox.application, naviox.module);
	});		
}

naviox.initModulesList = function() { 
	$('#modules_list_core .module-row').on( "click", function() {
  		$(this).find(".module-loading").show();
	});
	$('#more_modules').on( "click", function() {
		$('#loading_more_modules').show(); 
		$('#load_more_modules').hide();
	});
	$('#display_all_modules').on( "click", function() {
		naviox.displayAllModulesList($(this).data("search-word"));
	});
	$('#back_folder').on( "click", function() {
		naviox.goBack();
	});
	$('#home_folder').on( "click", function() {
		naviox.goHome();
	});	
	$('.folder-link').on( "click", function() {
		naviox.goFolder($(this).data('folder-id'));
	});		
}

naviox.initBookmark = function() { 
	$('#bookmark').on( "click", function() {
		naviox.bookmark();
	});
}

naviox.initModuleHeader = function() {
    var closeIcons = $('.module-header-tab .close-icon');
    closeIcons.each(function(index, icon) {
        $(icon).on('click', function() {
            var module = $(icon).closest('.module-header-tab');
            var moduleList = $('.module-header-tab');
            var index = moduleList.index(module);
            if (module.length) {
                var selected = module.find('.selected');
                naviox.closeModule(naviox.application, naviox.module, index);
                if (selected.length) {
                    var nextElement = module.next().length ? module.next() : module.prev();
                    if (nextElement.length) {
                        var aElement = nextElement.find('a');
                        if (aElement.length) {
                            aElement.get(0).click();
                        }
                    }
                }
                module.addClass('hidden');
                setTimeout(function() {
                    module.get(0).remove();
                }, 500);
            }

        });
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
		naviox.bookmarkCurrentModule();
	}
	else if (naviox.changeBookmark(bookmark, bookmarkClass, "star", "star-outline")) {
		naviox.unbookmarkCurrentModule();
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
	naviox.modulesFilter($("#search_modules_text").val(), naviox.refreshSearchModulesList);
}

naviox.displayModulesList = function() { 
	naviox.modulesDisplayModulesList(naviox.refreshModulesList);  
}

naviox.displayAllModulesList = function(searchWord) {  
	naviox.modulesDisplayAllModulesList(searchWord, naviox.refreshModulesList);  
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
	naviox.foldersGoFolder(folderOid, naviox.refreshFolderModulesList);
}

naviox.goBack = function() { 
	naviox.foldersGoBack(naviox.refreshFolderBackModulesList);
}

naviox.goHome = function() { 
	naviox.foldersGoHome(naviox.refreshFolderBackModulesList);
}

naviox.refreshModulesList = function(modulesList) { 
	if (modulesList == null) {
		window.location=openxava.location="..";
		return;
	}
	$('#modules_list_core').html(modulesList);
	$('#modules_list_header').show();
	$('#modules_list_search_header').hide();
	naviox.initModulesList(); 
}

naviox.refreshSearchModulesList = function(modulesList) { 
	if (modulesList == null) {
		window.location=openxava.location="..";
		return;
	}
	$('#modules_list_core').html(modulesList);
	$('#modules_list_header').hide();
	$('#modules_list_search_header').show();
	naviox.initModulesList(); 	
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
    		box.css("left", "0");
    		naviox.postRefreshFolderModulesList(); 
    	}
    );
}

naviox.postRefreshFolderModulesList = function() { 
	$('#modules_list_content').children().first().remove();
	naviox.reinitModulesList();
}

naviox.postRefreshFolderBackModulesList = function() { 
	$('#modules_list_content').children().last().remove();
	naviox.reinitModulesList();
}

naviox.reinitModulesList = function() { 
	naviox.watchSearch();
	$('.modules-list-header').css("width", "100%");
	naviox.initModulesList(); 
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
    		naviox.postRefreshFolderBackModulesList(); 
    	}
    );
}

naviox.modulesDisplayModulesList = function(callback) {
	var params = new URLSearchParams();
	params.append("operation", "displayModulesList");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	openxava.post("/xava/modules", params, callback);
}

naviox.modulesDisplayAllModulesList = function(searchWord, callback) {
	var params = new URLSearchParams();
	params.append("operation", "displayAllModulesList");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	params.append("searchWord", searchWord);
	openxava.post("/xava/modules", params, callback);
}

naviox.modulesFilter = function(searchWord, callback) {
	var params = new URLSearchParams();
	params.append("operation", "filter");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	params.append("searchWord", searchWord);
	openxava.post("/xava/modules", params, callback);
}

naviox.bookmarkCurrentModule = function() {
	var params = new URLSearchParams();
	params.append("operation", "bookmarkCurrentModule");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	openxava.post("/xava/modules", params);
}

naviox.unbookmarkCurrentModule = function() {
	var params = new URLSearchParams();
	params.append("operation", "unbookmarkCurrentModule");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	openxava.post("/xava/modules", params);
}

naviox.closeModule = function(application, module, index) {
	var params = new URLSearchParams();
	params.append("operation", "closeModule");
	params.append("application", application);
	params.append("module", module);
	params.append("index", index);
	openxava.post("/xava/modules", params);
}

naviox.foldersGoFolder = function(folderOid, callback) {
	var params = new URLSearchParams();
	params.append("operation", "goFolder");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	params.append("folderOid", folderOid);
	openxava.post("/xava/folders", params, callback);
}

naviox.foldersGoBack = function(callback) {
	var params = new URLSearchParams();
	params.append("operation", "goBack");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	openxava.post("/xava/folders", params, callback);
}

naviox.foldersGoHome = function(callback) {
	var params = new URLSearchParams();
	params.append("operation", "goHome");
	params.append("application", naviox.application);
	params.append("module", naviox.module);
	openxava.post("/xava/folders", params, callback);
}


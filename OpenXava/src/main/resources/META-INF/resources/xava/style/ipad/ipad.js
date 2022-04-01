if (ipad == null) var ipad = {};

ipad.onLoad = function() {
	var id = $('body').data('ipad-slide-element');
	var clazz = $('body').data('ipad-slide-class');	
	$('#' + id).addClass(clazz);
	setTimeout("$('#" + id + "').removeClass('" + clazz + "')", 0);
	$('body').removeData();
}

ipad.onClickNextPage = function(listId) {	
	$('#' + listId).addClass('slided-left');
	$('body').data('ipad-slide-element', listId);
	$('body').data('ipad-slide-class', 'slided-right');
}

ipad.onClickPreviousPage = function(listId) {
	$('#' + listId).addClass('slided-right');
	$('body').data('ipad-slide-element', listId);
	$('body').data('ipad-slide-class', 'slided-left');
}

// Currently ipad.setHtml is not used because of performance issues
ipad.setHtml = function(id, content) { 
	if (!id.match(/__core$/)) {
		$("#" + id).html(content);
		return;
	}								

	// Core
	$("#container").addClass("container");
	$("#sheet").addClass("sheet");
	$("#front").addClass("face");
	$("#back").addClass("face");
	
	if ($("#sheet").hasClass("flipped")) {
		$("#" + id).html(content);
		$("#sheet").removeClass("flipped");
	}
	else {
		$("#core_BACK").html(content);
		$("#sheet").addClass("flipped");
	}	
	
	$('#sheet').bind('webkitTransitionEnd', 
		function( event ) {			
			if ($("#sheet").hasClass("flipped")) $("#" + id).empty();
			else {
				$("#core_BACK").empty();
				$("#container").removeClass("container");
				$("#sheet").removeClass("sheet");
				$("#front").removeClass("face");
				$("#back").removeClass("face");				
			}
			$('#sheet').unbind();
		}, 
		false 
	);
		
}

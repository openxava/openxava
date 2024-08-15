openxava.addEditorInitFunction(function() {
    $('.xava_popup_menu_icon').each(function() {
        var popUpMenuIcon = $(this);
        var popUpMenu = popUpMenuIcon.next('.ox-popup-menu');

        popUpMenuIcon.on('click', function(event) {
            popUpMenu.toggleClass("ox-display-none");
            var buttonOffset = popUpMenuIcon.offset();
			var buttonRect = popUpMenuIcon.get(0).getBoundingClientRect();
            var positionPopup = popUpMenu.get(0).getBoundingClientRect();
			var left = buttonRect.left; 
			if (buttonRect.left + positionPopup.width > window.innerWidth) {
				left -= (positionPopup.width - buttonRect.width);
			}
            popUpMenu.css({
                top: buttonOffset.top + popUpMenuIcon.outerHeight(),
                left: left 
            });            
        });
		
		if (popUpMenu) {
			popUpMenu.on('click', 'a', function(event) {
				popUpMenu.addClass("ox-display-none");
			});
		}


        $(document).on('click', function(event) {
            if (!popUpMenuIcon.is(event.target) && popUpMenuIcon.has(event.target).length === 0 &&
                !popUpMenu.is(event.target) && popUpMenu.has(event.target).length === 0) {
                popUpMenu.addClass("ox-display-none");
            }
        });
    });	
	
});
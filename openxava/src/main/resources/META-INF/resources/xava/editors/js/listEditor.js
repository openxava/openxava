if (listEditor == null) var listEditor = {};

// tmr ¿Renombrar listEditor.js como popupMenu.js?
// tmr También:
// tmr - Id debería ser único
// tmr - Al pulsar en el propio botón no se oculta.

openxava.addEditorInitFunction(function() {
    $('.xava_popup_menu_icon').each(function() {
        var popUpMenuIcon = $(this);
        var popUpMenu = popUpMenuIcon.next('#xava_popup_menu');

        popUpMenuIcon.on('click', function(event) {
            popUpMenu.removeClass("ox-display-none");
            var buttonOffset = popUpMenuIcon.offset();
            /* tmr
            popUpMenu.css({
                top: buttonOffset.top + popUpMenuIcon.outerHeight(),
                left: buttonOffset.left
            });
            */
            // tmr ini
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
            // tmr fin
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
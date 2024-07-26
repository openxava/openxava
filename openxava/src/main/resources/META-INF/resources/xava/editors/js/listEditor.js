if (listEditor == null) var listEditor = {};

openxava.addEditorInitFunction(function() {
    $('.xava_popup_menu_icon').each(function() {
        var popUpMenuIcon = $(this);
        var popUpMenu = popUpMenuIcon.next('#xava_popup_menu');

        popUpMenuIcon.on('click', function(event) {
            popUpMenu.removeClass("ox-display-none");
            var buttonOffset = popUpMenuIcon.offset();
            // tmr ini
            // TMR ¿RENOMBRAR listEditor.js como popupMenu.js?
            var positionPopup = popUpMenu.get(0).getBoundingClientRect();
			var left = buttonOffset.left; 
			if (buttonOffset.left + positionPopup.width > window.innerWidth) {
				// TMR ME QUEDÉ POR AQUÍI: PARA HACER ESTO, PARA QUE EL MENÚ SALGA DENTRO
			}
            // tmr fin
            popUpMenu.css({
                top: buttonOffset.top + popUpMenuIcon.outerHeight(),
                // tmr left: buttonOffset.left
                left: left // tmr
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
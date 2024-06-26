if (listEditor == null) var listEditor = {};

openxava.addEditorInitFunction(function() {
    $('.xava_popup_menu_icon').each(function() {
        var popUpMenuIcon = $(this);
        var popUpMenu = popUpMenuIcon.next('#xava_popup_menu');

        popUpMenuIcon.on('click', function(event) {
            popUpMenu.removeClass("ox-display-none");
            var buttonOffset = popUpMenuIcon.offset();
            popUpMenu.css({
                top: buttonOffset.top + popUpMenuIcon.outerHeight(),
                left: buttonOffset.left
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
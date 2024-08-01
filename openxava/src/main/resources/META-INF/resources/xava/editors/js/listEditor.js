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
	
	$(document).ready(function() {
        if ($('tr.ox-list-header').length) {
            var $table = $('table.ox-list');
            $table.floatThead({
                scrollContainer: function($table) {
					console.log("scrolling");
                    return $table.closest('.overflowdiv');
                },
				position: 'auto'
            });
        }
    });
	
	 /*
	$(document).ready(function() {
    var buttonBar = $('.ox-button-bar');
    var headers = $('th.ox-list-header');

    var initialHeaderTop = headers.offset().top;
    var buttonBarHeight = buttonBar.outerHeight();
	
    function setHeaderPosition() {
		console.log(initialHeaderTop);
        var scrollTop = $(window).scrollTop();
        var buttonBarBottom = buttonBar.offset().top + buttonBarHeight;

        if (scrollTop > initialHeaderTop - buttonBarHeight) {
			console.log(1);
            headers.css('top', buttonBarHeight + 'px');
        } else {
			console.log(1);
            headers.css('top', 0 + 'px');
        }
    }

    // Establecer la posición inicial
    setHeaderPosition();

    // Actualizar la posición al hacer scroll
    $(window).scroll(function() {
        setHeaderPosition();
    });

    // Actualizar la posición al cambiar el tamaño de la ventana
    $(window).resize(function() {
        setHeaderPosition();
    });
    });*/

});
openxava.addEditorInitFunction(function() {
    $(document).off('click.xavaPopupMenu');
    $('.xava_popup_menu_icon').off('click.xavaPopupMenu');
    $('.ox-popup-menu').off('click.xavaPopupMenu', 'a');
    
    $(document).on('click.xavaPopupMenu', '.xava_popup_menu_icon', function(event) {
        var popUpMenuIcon = $(this);
        var popUpMenu = popUpMenuIcon.next('.ox-popup-menu');
        
        popUpMenu.toggleClass("ox-display-none");
        if (popUpMenu.hasClass("ox-display-none")) return; 
        
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
    
    $(document).on('click.xavaPopupMenu', '.ox-popup-menu a', function(event) {
        $(this).closest('.ox-popup-menu').addClass("ox-display-none");
    });
    
    $(document).on('click.xavaPopupMenu', function(event) {
        var $target = $(event.target);
        if (!$target.hasClass('xava_popup_menu_icon') && 
            $target.parents('.xava_popup_menu_icon').length === 0 &&
            !$target.hasClass('ox-popup-menu') && 
            $target.parents('.ox-popup-menu').length === 0) {
            
            $('.ox-popup-menu').addClass("ox-display-none");
        }
    });
});
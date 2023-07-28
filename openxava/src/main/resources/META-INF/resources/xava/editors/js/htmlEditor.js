if (htmlEditor == null) var htmlEditor = {}; 

openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;	
	tinymce.init({
	  selector: '.ox-html-text',
	  plugins: 'link', 
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  language: openxava.language,
	  promotion: false,
	  branding: false,
	  init_instance_callback: htmlEditor.setInlineStyles
	});
	tinymce.init({
	  selector: '.ox-simple-html-text',
	  plugins: 'link',
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  menubar: false,
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  language: openxava.language,
	  promotion: false,
	  branding: false,
	  init_instance_callback: htmlEditor.setInlineStyles
	});
	$('.xava-new-comment').each( function () {
		var editor = tinymce.get(this.id);
		editor.on('focus', (e) => {
			var id = "#" + this.id + "_buttons";
			$(id + " input").fadeIn();
			$('.ox-bottom-buttons').children().fadeOut(); 
			$('.ox-button-bar-button').fadeOut(); 
    	});
	});
});

openxava.addEditorPreRequestFunction(function() { 
	if (openxava.browser.htmlUnit) return;
	tinymce.triggerSave(); 
});

openxava.addEditorDestroyFunction(function() {
	if (openxava.browser.htmlUnit) return;
	tinymce.remove();
});

htmlEditor.setInlineStyles = function(input) {
	$('#tinymce span[data-mce-style]', $('.tox-edit-area iframe').contents()).each(function() { 
		$(this).prop('style', $(this).data('mce-style')) 
	});
}

$(document).on('focusin', function(e) {
   	if ($(e.target).closest(".tox-tinymce, .tox-tinymce-aux, .moxman-window, .tam-assetmanager-root").length) {
      	e.stopImmediatePropagation();
   	}
});



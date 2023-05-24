openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;	
	tinymce.init({
	  selector: '.ox-html-text',
	  plugins: 'link', 
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  language: openxava.language,
	  promotion: false,
	  branding: false
	});
	tinymce.init({
	  selector: '.ox-simple-html-text',
	  plugins: 'link',
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  menubar: false,
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  language: openxava.language,
	  promotion: false,
	  branding: false
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
	/*
	To get all buttons
	 tinyMCE.activeEditor.ui.registry.getAll().buttons=" + tinyMCE.activeEditor.ui.registry.getAll().buttons); 
	 */
});

openxava.addEditorPreRequestFunction(function() { 
	if (openxava.browser.htmlUnit) return;
	tinymce.triggerSave(); 
});

openxava.addEditorDestroyFunction(function() {
	if (openxava.browser.htmlUnit) return;
	tinymce.remove();
});

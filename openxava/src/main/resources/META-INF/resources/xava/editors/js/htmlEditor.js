openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;	
	tinymce.init({
	  selector: '.ox-html-text',
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  language: openxava.language,
	  promotion: false,
	  branding: false
	});
	// TMR ME QUEDÉ POR AQUÍ, EL COMBO DE ESTILOS ACABÓ DE SALIR. ACORDARME DE LOS LINKS, LO PUEDO POSPONER PERO HE DE HACERLO
	tinymce.init({
	  selector: '.ox-simple-html-text',
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link image', /* tmr */
	  menubar: false, /* tmr */
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

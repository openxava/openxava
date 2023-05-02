// tmr ini
openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;	
	tinymce.init({
	  selector: '.ox-html-text',
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  language: openxava.language,
	  promotion: false,
	  branding: false
	});
	tinymce.init({
	  selector: '.ox-simple-html-text',
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
});

openxava.addEditorPreRequestFunction(function() { // tmr Documentar
	if (openxava.browser.htmlUnit) return;
	tinymce.triggerSave(); 
});

openxava.addEditorDestroyFunction(function() {
	if (openxava.browser.htmlUnit) return;
	tinymce.remove();
});
// tmr fin
/* tmr
openxava.addEditorInitFunction(function() {
	var config = { 
		language: openxava.language,
		uiColor: '#F5F5F5',
		title: false 
	};
	$('.ox-ckeditor').ckeditor(config);
	
	var simpleConfig = { 
		language: openxava.language,
		uiColor: '#F5F5F5',
		toolbarGroups: [
			{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
			{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
			{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
			{ name: 'forms', groups: [ 'forms' ] },
			{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
			{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
			{ name: 'links', groups: [ 'links' ] },
			{ name: 'insert', groups: [ 'insert' ] },
			{ name: 'styles', groups: [ 'styles' ] },
			{ name: 'colors', groups: [ 'colors' ] },
			{ name: 'tools', groups: [ 'tools' ] },
			{ name: 'others', groups: [ 'others' ] },
			{ name: 'about', groups: [ 'about' ] }
		],
		disableNativeSpellChecker: false,
		title: false,   
		removeButtons: 'Save,Templates,Cut,NewPage,Preview,Print,Copy,Paste,PasteText,PasteFromWord,Find,Replace,SelectAll,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,CreateDiv,Language,Anchor,Flash,PageBreak,Iframe,ShowBlocks,About,Undo,Redo,Subscript,Superscript,BidiLtr,BidiRtl,SpecialChar,Styles,Font,Scayt,Underline,Strike,BGColor,HorizontalRule' 
	};
	$('.ox-simple-ckeditor').ckeditor(simpleConfig);
	
	$('.xava-new-comment').each( function () {		 
		var editor = CKEDITOR.instances[this.id];		
		if (editor !== undefined) {
		 	editor.on( 'focus', function( e ) {			
				var id = "#" + $(e.editor.element).attr("id") + "_buttons";
				$(id + " input").fadeIn();
				$('.ox-bottom-buttons').children().fadeOut(); 
				$('.ox-button-bar-button').fadeOut(); 
			});
		}
	});
	
	// If you modify the next code: test modify a field with this editor and then click on new, should ask for confirmation
	$('.ox-ckeditor, .ox-simple-ckeditor').each( function () {		 
		var editor = CKEDITOR.instances[this.id];		
		if (editor !== undefined) {
		 	editor.on( 'change', function( e ) {
		 		openxava.dataChanged = true;
			});
		}
	});

});

openxava.addEditorDestroyFunction(function() {
	for (var instance in CKEDITOR.instances) {
		CKEDITOR.instances[instance].destroy(false); // Needs to be false, otherwise calculated properties in the same view reset editor content
	}
});
*/
if (htmlEditor == null) var htmlEditor = {}; 

htmlEditor.isDark = function() {
	return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
};

htmlEditor.injectContentStyles = function(editor) {
	var head = editor.getDoc().head;
	var rootStyle = getComputedStyle(document.documentElement);
	var vars = [
		'--font-family', '--font-size-md', '--line-height', '--color',
		'--background', '--frame-background', '--frame-border', '--input-background',
		'--accent-color', '--accent-soft', '--action-link-color'
	];
	var css = ':root {';
	for (var i = 0; i < vars.length; i++) {
		css += vars[i] + ': ' + rootStyle.getPropertyValue(vars[i]) + ';';
	}
	css += '}';
	css += 'body { font-family: var(--font-family); font-size: var(--font-size-md); line-height: var(--line-height); color: var(--color); background: var(--input-background); padding: var(--space-3); margin: 0; }';
	css += 'a { color: var(--action-link-color); }';
	var style = editor.getDoc().createElement('style');
	style.setAttribute('data-ox-content', 'true');
	style.textContent = css;
	head.appendChild(style);
};

openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;
	var skin = htmlEditor.isDark() ? 'oxide-dark' : 'oxide';
	tinymce.init({
	  selector: '.ox-html-text',
	  plugins: 'link', 
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  skin: skin,
	  language: openxava.language,
	  promotion: false,
	  branding: false,
	  init_instance_callback: function(editor) {
	  	htmlEditor.setInlineStyles(editor);
	  	htmlEditor.injectContentStyles(editor);
	  }
	});
	tinymce.init({
	  selector: '.ox-simple-html-text',
	  plugins: 'link',
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  menubar: false,
	  statusbar: false,
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  skin: skin,
	  language: openxava.language,
	  promotion: false,
	  branding: false,
	  init_instance_callback: function(editor) {
	  	htmlEditor.setInlineStyles(editor);
	  	htmlEditor.injectContentStyles(editor);
	  }
	});
	$('.xava-new-comment').each( function () {
		var editor = tinymce.get(this.id);
		editor.on('focus', (e) => {
			var id = this.id + "_buttons";
			if (openxava.browser.ff && openxava.dialogLevel > 0) {
				var position = document.getElementById(id).parentElement.getBoundingClientRect(); // Because jquery position() does not work well
				$('#'+id).css({
					'top': position.bottom - 42,
					'left': position.right - 290
				});	
			}
			$("#" + id + " input").fadeIn();
			$('.ox-bottom-buttons').css("visibility", "hidden");
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



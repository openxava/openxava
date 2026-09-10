if (htmlEditor == null) var htmlEditor = {}; 

htmlEditor.isDark = function() {
	var colorScheme = getComputedStyle(document.documentElement).getPropertyValue('color-scheme').trim();
	if (colorScheme === 'dark') return true;
	if (colorScheme === 'light') return false;
	return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
};

htmlEditor.resolveVar = function(rootStyle, name) {
	var value = rootStyle.getPropertyValue(name).trim();
	while (value.indexOf('var(') === 0) {
		var match = value.match(/^var\(\s*(--[\w-]+)\s*(?:,\s*(.+))?\)$/);
		if (!match) break;
		var varName = match[1];
		var fallback = match[2] ? match[2].trim() : '';
		var resolved = rootStyle.getPropertyValue(varName).trim();
		value = resolved || fallback;
		if (value === 'var(' + varName + ')') break;
	}
	return value;
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
		css += vars[i] + ': ' + htmlEditor.resolveVar(rootStyle, vars[i]) + ';';
	}
	css += '}';
	css += 'body, .mce-content-body { font-family: var(--font-family) !important; font-size: var(--font-size-md) !important; line-height: var(--line-height) !important; color: var(--color) !important; background: var(--background) !important; padding: var(--space-3) !important; margin: 0 !important; }';
	css += 'a { color: var(--action-link-color) !important; }';
	var style = editor.getDoc().createElement('style');
	style.setAttribute('data-ox-content', 'true');
	style.textContent = css;
	head.appendChild(style);
	
	var body = editor.getBody();
	if (body) {
		body.style.backgroundColor = htmlEditor.resolveVar(rootStyle, '--background');
		body.style.color = htmlEditor.resolveVar(rootStyle, '--color');
	}
};

openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;
	tinymce.init({
	  selector: '.ox-html-text',
	  plugins: 'link', 
	  toolbar: 'styles | bold italic forecolor | alignleft aligncenter alignright alignjustify | outdent indent | link', 
	  base_url: openxava.contextPath + '/xava/editors/tinymce/',
	  skin: 'oxide',
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
	  skin: 'oxide',
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



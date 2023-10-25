// tmr

openxava.addEditorInitFunction(function() {
	$('.ox-icons-list i').off('click').click(function() {
		if (!getSelection().toString()) {
			openxava.executeAction(openxava.lastApplication, openxava.lastModule, '', false, 
				"Icon.choose", "icon=" + $(this).attr('title'));
		}
	});
});


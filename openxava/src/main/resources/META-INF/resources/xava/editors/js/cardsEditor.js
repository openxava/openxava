// tmr Debería hacer la prueba manual
// tmr if (cardsEditor == null) var cardsEditor = {};

openxava.addEditorInitFunction(function() {
	$('.ox-card').off('click').click(function() {
		if (!getSelection().toString()) {
			openxava.executeAction(openxava.lastApplication, openxava.lastModule, false, false, 
				$(this).data('action'), "row=" + $(this).data('row'));
		}
	});
});


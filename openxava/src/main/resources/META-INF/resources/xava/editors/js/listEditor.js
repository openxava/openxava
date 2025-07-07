if (listEditor == null) var listEditor = {};

/**
 * JavaScript for listEditor.jsp
 * Handles events for editable cells in list view
 */
openxava.addEditorInitFunction(function() {
    // Find all editable cells in list view
    $('.ox-list-cell-editor').each(function() {
        var cellContainer = $(this);
        var row = cellContainer.data('row');
        var property = cellContainer.data('property');
        
        // Find all editors within this cell
        cellContainer.find('.editor').each(function() {
            var editor = $(this);         
            
            // Remove any existing change events
            editor.off('change');
            
            // Add our custom change event
            editor.on('change', function() {
                var newValue = editor.val();
                // Try different ways to get the name attribute
                var editorName = editor.attr('name');
                var editorNameProp = editor.prop('name');
                var editorNameDirect = editor[0] ? editor[0].name : 'No direct name';
                var editorNameGetAttribute = editor[0] ? editor[0].getAttribute('name') : 'No getAttribute';
                
                // TMR ME QUEDÉ POR AQUÍ: AL FINAL DESCUBRÍ QUE EL EVENTO LO LANZA UN SPAN PADRE
                console.log("V3"); // tmr
                console.log("newValue>" + newValue + "<");
                console.log("Editor HTML:", editor.prop('outerHTML'));
                console.log("editorName (attr):", editorName);
                console.log("editorName (prop):", editorNameProp);
                console.log("editorName (direct):", editorNameDirect);
                console.log("editorName (getAttribute):", editorNameGetAttribute);
                // If the value is undefined or empty and the name ends with __CONTROL__
                if ((newValue === undefined || newValue === "") && editorName && editorName.endsWith('__CONTROL__')) {
                    console.log("newValue is undefined and editorName ends with __CONTROL__"); // tmr
                    // Get the field name without the __CONTROL__ suffix
                    var baseFieldName = editorName.substring(0, editorName.length - '__CONTROL__'.length);
                    // Find the field with that id and get its value
                    var hiddenField = $('#' + baseFieldName);
                    if (hiddenField.length > 0) {
                        newValue = hiddenField.val();
                        console.log("Using value from hidden field: " + baseFieldName + " = " + newValue); // tmr
                    }
                }
                
                console.log("newValue<" + newValue);
                console.log("editor.name=" + editorName);
                
                // Call the DWR method to update the value in the server
                Tab.updateValue(openxava.lastApplication, openxava.lastModule, row, property, newValue, listEditor.showMessage);
                listEditor.lastEditor = editor;
                editor.parent().removeClass("ox-error-editor");
            });
        });
    });
});

listEditor.showMessage = function(message) {
	if (message.startsWith("ERROR:")) {
		var errorMessage = message.substring(6).trim(); // Remove "ERROR:" prefix and trim
		openxava.showError(errorMessage);
		listEditor.lastEditor.parent().addClass("ox-error-editor");
	}
	else {
		openxava.showMessage(message);
	}
}

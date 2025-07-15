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
                
                // Special case for ox-descriptions-list editors
                if (editor.hasClass('ox-descriptions-list')) {
                    // Get the value from the first hidden input field
                    var hiddenInput = editor.find('input[type="hidden"]').first();
                    if (hiddenInput.length > 0) {
                        newValue = hiddenInput.val();
                    }
                }                
                
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

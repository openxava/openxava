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
                                
                // Call the DWR method to update the value in the server
                Tab.updateValue(openxava.lastApplication, openxava.lastModule, row, property, newValue, listEditor.showMessage);
                
            });
        });
    });
});

listEditor.showMessage = function(message) {
	if (message.startsWith("ERROR:")) {
		var errorMessage = message.substring(6).trim(); // Remove "ERROR:" prefix and trim
		openxava.showError(errorMessage);
	}
	else {
		openxava.showMessage(message);
	}
}

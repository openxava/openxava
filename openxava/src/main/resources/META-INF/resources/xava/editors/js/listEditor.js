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
                listEditor.lastRow = row;
                listEditor.lastProperty = property;
                listEditor.lastEditor = editor;
                Tab.updateValue(openxava.lastApplication, openxava.lastModule, row, property, newValue, listEditor.showMessage);
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
		var parts = message.split("\t");
		var displayMessage = parts[0];
		if (parts.length >= 4 && parts[1].startsWith("UNDO:")) {
			var undoLabel = parts[1].substring(5);
			listEditor.undoOldValue = parts[2];
			listEditor.undoRestoreMessage = parts[3];
			listEditor.undoRow = listEditor.lastRow;
			listEditor.undoProperty = listEditor.lastProperty;
			displayMessage += ' <a href="#" class="ox-undo-link">'
				+ undoLabel + '</a>';
		}
		openxava.showMessage(displayMessage);
		$('.ox-undo-link').off('click').on('click', function(e) {
			e.preventDefault();
			listEditor.undo();
		});
	}
}

listEditor.undo = function() {
	var row = listEditor.undoRow;
	var property = listEditor.undoProperty;
	var oldValue = listEditor.undoOldValue;
	listEditor.lastRow = row;
	listEditor.lastProperty = property;
	// Find the editor for this row and property and restore its displayed value
	$('.ox-list-cell-editor').each(function() {
		var cell = $(this);
		if (cell.data('row') == row && cell.data('property') == property) {
			var editor = cell.find('.editor').first();
			if (editor.length > 0) {
				listEditor.lastEditor = editor;
				if (editor.hasClass('ox-descriptions-list')) {
					var hiddenInput = editor.find('input[type="hidden"]').first();
					if (hiddenInput.length > 0) {
						descriptionsEditor.val(hiddenInput, oldValue);
					}
				}
				else {
					editor.val(oldValue);
				}
			}
		}
	});
	Tab.updateValue(openxava.lastApplication, openxava.lastModule, row, property, oldValue, listEditor.showUndoResult);
}

listEditor.showUndoResult = function(message) {
	if (message.startsWith("ERROR:")) {
		var errorMessage = message.substring(6).trim();
		openxava.showError(errorMessage);
	}
	else {
		openxava.showMessage(listEditor.undoRestoreMessage);
	}
}

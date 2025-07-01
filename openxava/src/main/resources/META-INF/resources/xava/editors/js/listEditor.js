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
                
                // Get application and module from the URL
                var url = window.location.href;
                var urlParams = new URLSearchParams(url.substring(url.indexOf('?') + 1));
                var application = urlParams.get('application');
                var module = urlParams.get('module');
                
                // In OpenXava, the default tabObject is 'xava_tab' unless specified otherwise
                var tabObject = 'xava_tab';
                // If we're in a collection, the tabObject might be different
                var collectionPrefix = $('#xava_collection_prefix').val();
                if (collectionPrefix) {
                    tabObject = collectionPrefix + 'tab';
                }
                
                // TMR ME QUEDÉ POR AQUÍ: FALLA, POSIBLEMENTE document Y location NO SON CORRECTOS
                // Call the DWR method to update the value in the server
                Tab.updateValue(document, location, application, module, row, property, newValue, tabObject);
                
                // We could add visual feedback here to indicate the change was sent to the server
            });
        });
    });
});

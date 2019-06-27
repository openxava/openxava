/* tmp ¿ Generalizar para todos los files ? */

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);
    
    // tmp ¿Funciona con más de una PHOTO en la vista?
	
    const inputs = document.querySelectorAll('.xava_image');
    
    inputs.forEach(function(input) {
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input);
	    	pond.addFile(input.dataset.url);
    	}
    });
	
});

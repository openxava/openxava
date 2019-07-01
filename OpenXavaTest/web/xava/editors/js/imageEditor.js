/* tmp ¿ Generalizar para todos los files ? */

if (imageEditor == null) var imageEditor = {};

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);
    
    // tmp ¿Funciona con más de una PHOTO en la vista?
	
    const inputs = document.querySelectorAll('.xava_image');
    
    inputs.forEach(function(input) {
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input);
	    	if (input.dataset.url !== "") {
	    		pond.addFile(input.dataset.url);
	    	}
	    	else {
	    		imageEditor.enableUpload(pond, input);
	    	}
	    	pond.onremovefile = function() {
	    		imageEditor.enableUpload(pond, input);
	    		$.ajax({
	    			url: "../xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module,
	    			method: "DELETE"
    			})
    		}
    	}
 
    });
	
});

/* tmp ¿Poner destroy function? */

imageEditor.enableUpload = function(pond, input) {
	pond.setOptions({ 
	    server: {
	    	process: "../xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module
	    }
	});
}

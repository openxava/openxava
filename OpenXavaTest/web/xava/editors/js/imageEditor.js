/* tmp ¿ Generalizar para todos los files ? */

if (imageEditor == null) var imageEditor = {};

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);
        
    const inputs = document.querySelectorAll('.xava_image');
    inputs.forEach(function(input) {
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input);
	    	if (input.dataset.empty !== "true") {
	    		pond.addFile(imageEditor.getImageURL(input));
	    	}	    	
	    	else {
	    		imageEditor.enableUpload(pond, input);
	    	}
	    	pond.onremovefile = function() {
	    		imageEditor.enableUpload(pond, input);
	    		$.ajax({
	    			url: imageEditor.getUploadURL(input),
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
	    	process: imageEditor.getUploadURL(input) 
	    }
	});
}

imageEditor.getUploadURL = function(input) {
	return "../xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&property=" + input.dataset.property;
}

imageEditor.getImageURL = function(input) {
	return "../xava/ximage?application=" + input.dataset.application + "&module=" + input.dataset.module + "&property=" + input.dataset.property;
}

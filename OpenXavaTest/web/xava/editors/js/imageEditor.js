/* tmp ¿ Generalizar para todos los files ? */

if (imageEditor == null) var imageEditor = {};

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);

    $('.xava_image').each(function() {
    	const input = this;
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input); // tmp ¿var en vez de const?
	    	if (typeof pond === 'undefined') return; // tmp
	    	const imageURL = imageEditor.getImageURL(input);
	    	pond.onactivatefile = function() {
	    		window.open(imageURL); 
	    	}	    	
	    	if (input.dataset.empty !== "true") {
	    		pond.addFile(imageURL);
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
	    	if (input.dataset.editable === "true") {
	    		pond.disabled = true; 
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
	return "../xava/ximage?application=" + input.dataset.application + "&module=" + input.dataset.module + "&property=" + input.dataset.property + "&dif=" + new Date().getTime();
}

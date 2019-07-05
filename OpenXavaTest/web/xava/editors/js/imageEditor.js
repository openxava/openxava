/* tmp ¿ Generalizar para todos los files ? */

if (imageEditor == null) var imageEditor = {};

openxava.addEditorInitFunction(function() {

	console.log("[openxava.addEditorInitFunction] FilePondPluginImagePreview >>"); // tmp
    FilePond.registerPlugin(FilePondPluginImagePreview);
    console.log("[openxava.addEditorInitFunction] FilePondPluginImagePreview <<"); // tmp
    
    $('.xava_image').each(function() {
    	console.log("[openxava.addEditorInitFunction] Entro"); // tmp
    	const input = this;
    	console.log("[openxava.addEditorInitFunction] input=" + input); // tmp
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input);
	    	if (input.dataset.empty !== "true") {
	    		console.log("[openxava.addEditorInitFunction] A"); // tmp
	    		var url = imageEditor.getImageURL(input);
	    		console.log("[openxava.addEditorInitFunction] url=" + url); // tmp
	    		pond.addFile(url);
	    	}	    	
	    	else {
	    		console.log("[openxava.addEditorInitFunction] B"); // tmp
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
	    	// tmp ini
	    	/* tmp
	    	pond.onactivatefile = function() {
	    		// tmp window.open(imageEditor.getImageURL(input));
	    		window.open(imageEditor.getImageURL(input)); // tmp
	    	}
	    	*/
	    	// tmp fin
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
	return "../xava/ximage?application=" + input.dataset.application + "&module=" + input.dataset.module + "&property=" + input.dataset.property + "&dif=" + new Date().getMilliseconds();
}

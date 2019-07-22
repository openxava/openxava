if (imageEditor == null) var imageEditor = {};

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);

    $('.xava_image').each(function() {
    	const input = this;
    	if (FilePond.find(input) == null) {
    		if ($(input).is(":hidden")) return;
	    	const pond = FilePond.create(input); 
	    	if (typeof pond === 'undefined') return; 
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
	    	pond.dropValidation = true;
	    	pond.beforeDropFile = function() {
	    		imageEditor.enableUpload(pond, input);
	    		return true;
	        }
	    	pond.allowRevert = false; 
    	}
    	
    });
	
});

imageEditor.enableUpload = function(pond, input) {
	pond.setOptions({ 
	    server: {
	    	process: imageEditor.getUploadURL(input) 
	    }
	});
}

imageEditor.getUploadURL = function(input) {
	return "/" + openxava.lastApplication + "/xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id;
}

imageEditor.getImageURL = function(input) {
	return "/" + openxava.lastApplication + "/xava/ximage?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id + "&dif=" + new Date().getTime();
}

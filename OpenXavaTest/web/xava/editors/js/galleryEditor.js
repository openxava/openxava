/* tmp ¿Fusionar con imageEditor? */

if (galleryEditor == null) var galleryEditor = {};

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);

    $('.xava_gallery').each(function() {
    	const input = this;
    	if (FilePond.find(input) == null) {
    		if ($(input).is(":hidden")) return;
	    	const pond = FilePond.create(input); 
	    	if (typeof pond === 'undefined') return; 
	    	const imageURL = galleryEditor.getImageURL(input);
	    	pond.onactivatefile = function() {
	    		window.open(imageURL); 
	    	}	    	
	    	if (input.dataset.empty !== "true") {
	    		const images = input.dataset.images.split(",");
	    		for (image of images) {
	    			console.log("[openxava.addEditorInitFunction] image=" + image);
	    			const url = "/" + openxava.lastApplication + "/xava/gallery?application=" + input.dataset.application + "&module=" + input.dataset.module + "&oid=" + image + "&dif=" + new Date().getTime();
	    			console.log("[openxava.addEditorInitFunction] url=" + url);
	    			pond.addFile(url);
	    		}
	    	}	    	
	    	else {
	    		galleryEditor.enableUpload(pond, input);
	    	}
	    	pond.onremovefile = function() {
	    		galleryEditor.enableUpload(pond, input);
	    		$.ajax({
	    			url: galleryEditor.getUploadURL(input),
	    			method: "DELETE"
    			})
    		}
	    	if (input.dataset.editable === "true") {
	    		pond.disabled = true; 
	    	}
	    	pond.dropValidation = true;
	    	pond.beforeDropFile = function() {
	    		galleryEditor.enableUpload(pond, input);
	    		return true;
	        }
	    	pond.allowRevert = false; 
    	}
    	
    });
	
});

galleryEditor.enableUpload = function(pond, input) {
	pond.setOptions({ 
	    server: {
	    	process: galleryEditor.getUploadURL(input) 
	    }
	});
}

galleryEditor.getUploadURL = function(input) {
	return "/" + openxava.lastApplication + "/xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id;
}

galleryEditor.getImageURL = function(input) { // tmp ¿Quitar?
	return "/" + openxava.lastApplication + "/xava/ximage?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id + "&dif=" + new Date().getTime();
}

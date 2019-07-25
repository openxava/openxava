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
	    	pond.allowMultiple = true; // tmp
	    	// tmp const imageURL = galleryEditor.getImageURL(input);
	    	const imageURL = "/" + openxava.lastApplication + "/xava/gallery?application=" + input.dataset.application + "&module=" + input.dataset.module + "&dif=" + new Date().getTime(); 
	    	pond.onactivatefile = function(file) {	    		
	    		window.open(imageURL + "&oid=" + file.getMetadata("imageOid")); 
	    	}	    	
	    	if (input.dataset.empty !== "true") {
	    		const images = input.dataset.images.split(",");
	    		for (image of images) {
	    			const url = "/" + openxava.lastApplication + "/xava/gallery?application=" + input.dataset.application + "&module=" + input.dataset.module + "&oid=" + image + "&dif=" + new Date().getTime();
	    			const file = pond.addFile(url, {metadata: { imageOid: image }}); // tmp ¿imageOid es buen nombre?
	    		}
	    	}	    	
	    	else {
	    		galleryEditor.enableUpload(pond, input);
	    	}
	    	// tmp pond.onremovefile = function() {
	    	pond.onremovefile = function(error, file) { // tmp
	    		galleryEditor.enableUpload(pond, input);
	    		$.ajax({
	    			// tmp url: galleryEditor.getUploadURL(input),
	    			url: galleryEditor.getUploadURL(input) + "&imageOid=" + file.getMetadata("imageOid"), // tmp Esto debería hacerlo mejor
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

if (uploadEditor == null) var uploadEditor = {};

openxava.addEditorInitFunction(function() {
	
    FilePond.registerPlugin(FilePondPluginImagePreview);

    $('.xava_upload').each(function() {
    	const input = this;
    	if (FilePond.find(input) == null) {
    		if ($(input).is(":hidden")) return;
	    	const pond = FilePond.create(input); 
	    	if (typeof pond === 'undefined') return;
	    	if (input.dataset.mutiple === "true") pond.allowMultiple = true;
	    	if (input.dataset.preview === "false") pond.allowImagePreview = false; 
	    	const fileURL = uploadEditor.getFileURL(input);
	    	pond.onactivatefile = function(file) {
	    		if (openxava.browser.ie) window.open(fileURL + uploadEditor.getFileIdParam(file)); 
	    		else if (pond.allowImagePreview) {
	    			if (openxava.browser.ff) {
		    			openxava.setUrlParam("");
		    			window.location = URL.createObjectURL(file.file);
		    		}
		    		else window.open(URL.createObjectURL(file.file));
	    		}
	    		else {
	    			var link = document.createElement('a');
	    			link.href = URL.createObjectURL(file.file);
	    			link.download = file.filename;
	    			link.dispatchEvent(new MouseEvent('click'));
	    		}
	    	}	    	
	    	if (input.dataset.empty !== "true") {
	    		if (typeof input.dataset.files !== 'undefined') {
		    		const filesIds = input.dataset.files.split(",");
		    		filesIds.forEach(function(fileId) {
		    			const url = fileURL + "&fileId=" + fileId;
		    			pond.addFile(url, {metadata: { fileId: fileId }}); 		    			
		    		});
	    		}
	    		else {
	    			pond.addFile(fileURL);
	    		}
	    	}
	    	else {
	    		uploadEditor.enableUpload(pond, input);
	    	}
	    	pond.onremovefile = function(error, file) { 
	    		uploadEditor.enableUpload(pond, input);
	    		$.ajax({
	    			url: uploadEditor.getUploadURL(input) + uploadEditor.getFileIdParam(file), 
	    			method: "DELETE"
    			})
    		}
	    	if (input.dataset.editable === "true") {
	    		pond.disabled = true; 
	    	}
	    	pond.dropValidation = true;
	    	pond.beforeDropFile = function() {
	    		uploadEditor.enableUpload(pond, input);
	    		return true;
	        }
	    	if (input.dataset.throwsChanged === "true") {
		    	pond.onprocessfile = function(error, file) {
		    		openxava.throwPropertyChanged(input.dataset.application, input.dataset.module, input.id);
		    	}	    	
		    	pond.onremovefile = function(error, file) {
		    		openxava.throwPropertyChanged(input.dataset.application, input.dataset.module, input.id);
		    	}	    		    	
	    	}
	    	pond.allowRevert = false;
    	}
    	
    });
	
});

uploadEditor.enableUpload = function(pond, input) {
	pond.setOptions({ 
	    server: {
	    	process: uploadEditor.getUploadURL(input) 
	    }
	});
}

uploadEditor.getUploadURL = function(input) {
	return "/" + openxava.lastApplication + "/xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id;
}

uploadEditor.getFileURL = function(input) { 
	return "/" + openxava.lastApplication + "/xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id + "&dif=" + new Date().getTime();
}

uploadEditor.getFileIdParam = function(file) {
	const fileId = file.getMetadata("fileId");
	return typeof fileId === 'undefined'?"":"&fileId=" + fileId; 
}

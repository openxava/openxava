if (uploadEditor == null) var uploadEditor = {};

openxava.addEditorInitFunction(function() {
	
    FilePond.registerPlugin(FilePondPluginImagePreview);

    $('.xava_upload').each(function() {
    	const input = this;
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input); 
	    	if (typeof pond === 'undefined') return;
	    	if (input.dataset.mutiple === "true") pond.allowMultiple = true;
	    	if (input.dataset.preview === "false") pond.allowImagePreview = false; 
	    	if (input.dataset.application == null) {
	    		var id = input.id.split("_", 4);	    		
	    		input.dataset.application = id[1];
	    		input.dataset.module = id[2];
	    		input.dataset.empty = true;
	    	}
	    	const fileURL = uploadEditor.getFileURL(input);
	    	pond.onactivatefile = function(file) {
	    		if (openxava.browser.edge || openxava.browser.ie) window.open(fileURL + uploadEditor.getFileIdParam(file)); 
	    		else  if (pond.allowImagePreview) {
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
	    		var count = 0; 
	    		if (typeof input.dataset.files !== 'undefined') {
		    		const filesIds = input.dataset.files.split(",");
		    		filesIds.forEach(function(fileId) {
		    			const url = fileURL + "&fileId=" + fileId;
		    			count++; 
		    			pond.addFile(url, {metadata: { fileId: fileId }}); 		    			
		    		});
	    		}
	    		else {
	    			count = 1; 
	    			pond.addFile(fileURL);
	    		}
	    		
	    		var c = 1;
	    		pond.onaddfile = function() {
	    			if (c++ === count) {
	    				uploadEditor.enableUpload(pond, input);
	    			}
	    		} 

	    	}
	    	else {
	    		uploadEditor.enableUpload(pond, input);
	    	}
	    	pond.onremovefile = function(error, file) { 
    			uploadEditor.removeFile(input, file); 
    		}
	    	if (input.dataset.editable === "true") {
		    	pond.allowDrop = false;
		    	pond.allowBrowse = false;
		    	pond.allowPaste = false;
	    	}
	    	if (input.dataset.throwsChanged === "true") {
		    	pond.onprocessfile = function(error, file) {
		    		openxava.throwPropertyChanged(input.dataset.application, input.dataset.module, input.id);
		    	}	    	
		    	pond.onremovefile = function(error, file) {
		    		uploadEditor.removeFile(input, file);
		    		openxava.throwPropertyChanged(input.dataset.application, input.dataset.module, input.id);
		    	}	    		    	
	    	}
	    	pond.allowRevert = false;
		    pond.onerror = function(error) {
	    	if (error && error.code == 406) {
					openxava.ajaxRequest(input.dataset.application, input.dataset.module, false, false);
			    }
		    }
		    
		    pond.onaddfilestart = function() {
		    	openxava.hideErrors(input.dataset.application, input.dataset.module);
		    }		    
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
	return openxava.contextPath + "/xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id + "&windowId=" + $('#xava_window_id').val();
}

uploadEditor.getFileURL = function(input) { 
	return openxava.contextPath + "/xava/upload?application=" + input.dataset.application + "&module=" + input.dataset.module + "&propertyKey=" + input.id + "&dif=" + new Date().getTime() + "&windowId=" + $('#xava_window_id').val();
}

uploadEditor.getFileIdParam = function(file) {
	const fileId = file.getMetadata("fileId");
	return typeof fileId === 'undefined'?"":"&fileId=" + fileId; 
}

uploadEditor.removeFile = function(input, file) {
	$.ajax({
		url: uploadEditor.getUploadURL(input) + uploadEditor.getFileIdParam(file), 
		method: "DELETE"
	})
}

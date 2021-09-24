if (uploadEditor == null) var uploadEditor = {};

openxava.addEditorInitFunction(function() {
	
    FilePond.registerPlugin(FilePondPluginImagePreview);
    FilePond.registerPlugin(FilePondPluginFileValidateType); // tmr

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
	    		else if (pond.allowImagePreview && file.file.type.indexOf("image") == 0) { 
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
		    
		    pond.beforeRemoveFile = function() {
		    	return confirm(openxava.confirmRemoveFileMessage);   
		    }
		    // tmr ini
		    pond.fileValidateTypeLabelExpectedTypesMap = {
		        'text/plain': '.txt',
		        'text/csv': 'CSV',
		        '.csv': null,
		        'application/vnd.ms-excel': 'XLS', 
		        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'XLSX',
		        'application/pdf': '.pdf',
		        'application/doc': '.doc',
		        'application/rtf': null,
		        'application/x-iwork-pages-sffpages': '.pages'
		        /*
		        TMR ME QUEDÉ POR AQUÍ COMPLETANDO ESTA LISTA
		        	'text/html': 'HTML, HTM, SHTML',
    'text/css': 'CSS',
    'text/xml': 'XML',
    'image/gif': 'GIF',
    'image/jpeg': 'JPEG, JPG',
    'application/x-javascript': 'JS',
    'application/atom+xml': 'ATOM',
    'application/rss+xml': 'RSS',
    'text/mathml': 'MML',
    'text/plain': 'TXT',
    'text/vnd.sun.j2me.app-descriptor': 'JAD',
    'text/vnd.wap.wml': 'WML',
    'text/x-component': 'HTC',
    'image/png': 'PNG',
    'image/tiff': 'TIF, TIFF',
    'image/vnd.wap.wbmp': 'WBMP',
    'image/x-icon': 'ICO',
    'image/x-jng': 'JNG',
    'image/x-ms-bmp': 'BMP',
    'image/svg+xml': 'SVG',
    'image/webp': 'WEBP',
    'application/java-archive': 'JAR, WAR, EAR',
    'application/mac-binhex40': 'HQX',
    'application/msword': 'DOC',
    'application/pdf': 'PDF',
    'application/postscript': 'PS, EPS, AI',
    'application/rtf': 'RTF',
    'application/vnd.ms-excel': 'XLS',
	'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'XLSX',
    'application/vnd.ms-powerpoint': 'PPT',
    'application/vnd.wap.wmlc              wmlc',
    'application/vnd.google-earth.kml+xml  kml',
    'application/vnd.google-earth.kmz      kmz',
    'application/x-7z-compressed           7z',
    'application/x-cocoa                   cco',
    'application/x-java-archive-diff       jardiff',
    'application/x-java-jnlp-file          jnlp',
    'application/x-makeself                run',
    'application/x-perl                    pl pm',
    'application/x-pilot                   prc pdb',
    'application/x-rar-compressed          rar',
    'application/x-redhat-package-manager  rpm',
    'application/x-sea                     sea',
    'application/x-shockwave-flash         swf',
    'application/x-stuffit                 sit',
    'application/x-tcl                     tcl tk',
    'application/x-x509-ca-cert            der pem crt',
    'application/x-xpinstall               xpi',
    'application/xhtml+xml                 xhtml',
    'application/zip                       zip',
    'audio/midi                            mid midi kar',
    'audio/mpeg                            mp3',
    'audio/ogg                             ogg',
    'audio/x-realaudio                     ra',
    'video/3gpp                            3gpp 3gp',
    'video/mpeg                            mpeg mpg',
    'video/quicktime                       mov',
    'video/x-flv                           flv',
    'video/x-mng                           mng',
    'video/x-ms-asf                        asx asf',
    'video/x-ms-wmv                        wmv',
    'video/x-msvideo                       avi',
    'video/mp4                             m4v mp4',
		        */
		    }
		    
		    pond.fileValidateTypeDetectType = (source, type) => new Promise((resolve, reject) => {
    			if (type == "" && source.name.substr(-4).toLowerCase() === '.csv') {
    				type = "text/csv";
    			}
        		resolve(type);
    		})
		    // tmr fin
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

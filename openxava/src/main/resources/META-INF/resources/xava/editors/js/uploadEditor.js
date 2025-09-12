if (uploadEditor == null) var uploadEditor = {};

openxava.addEditorInitFunction(function() {
	
    FilePond.registerPlugin(FilePondPluginImagePreview);
    FilePond.registerPlugin(FilePondPluginFileValidateType);
    FilePond.registerPlugin(FilePondPluginFileValidateSize);  

    $('.xava_upload').each(function() {
        var idSelector = "";
        var classSelector = "";
        const input = this;
        const inputParent = input.parentElement.parentElement;    
        
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
                    } else window.open(URL.createObjectURL(file.file));
                } else {
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
                        pond.addFile(url, {
                            metadata: {
                                fileId: fileId
                            }
                        });
                    });
                } else {
                    count = 1;
                    pond.addFile(fileURL);
                }

                var c = 1;
                pond.onaddfile = function() {
                    if (c++ === count) {
                        uploadEditor.enableUpload(pond, input);
                        uploadEditor.resizeHeightWhenAddOrProcess(input);
                    }
                }
            } else {
                uploadEditor.enableUpload(pond, input);
                uploadEditor.resizeHeightWhenUploadOrRemove(input);
            }
            pond.onremovefile = function(error, file) {
                uploadEditor.removeFile(input, file);
                uploadEditor.resizeHeightWhenUploadOrRemove(input);
            }
            if (input.dataset.editable === "true") {
                pond.allowDrop = false;
                pond.allowBrowse = false;
                pond.allowPaste = false;
            }             
            if (inputParent.className === "xava_onchange") { 
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

            pond.onaddfilestart = function(file) {
                openxava.hideErrors(input.dataset.application, input.dataset.module);
            }

            pond.beforeRemoveFile = function() {
                return confirm(openxava.confirmRemoveFileMessage);
            }

            pond.fileValidateTypeLabelExpectedTypesMap = uploadEditor.fileValidateTypeLabelExpectedTypesMap;
            pond.fileValidateTypeDetectType = (source, type) => new Promise((resolve, reject) => {
                if (type == "" && source.name.substr(-4).toLowerCase() === '.csv') {
                    type = "text/csv";
                }
                resolve(type);
            })

            if (input.dataset.maxFileSize != null) {
                pond.maxFileSize = input.dataset.maxFileSize;
            }
            

            pond.onprocessfilestart = function () {
                uploadEditor.resizeHeightWhenAddOrProcess(input);
            }
        }
    });
    // Delegated handler for "Download all" to comply with CSP (no inline onclick)
    $(document)
        .off('click.oxDownloadAll', '.ox-download-all-link') 
        .on('click.oxDownloadAll', '.ox-download-all-link', function (e) {
            e.preventDefault();
            const $a = $(this);
            const app = $a.data('application');
            const module = $a.data('module');
            const propertyKey = $a.data('propertyKey');
            const windowId = $('#xava_window_id').val();
            if (!app || !module || !propertyKey) return; // defensive
            const url = `${openxava.contextPath}/xava/zipfiles?application=${encodeURIComponent(app)}&module=${encodeURIComponent(module)}&propertyKey=${encodeURIComponent(propertyKey)}&windowId=${encodeURIComponent(windowId || '')}`;
            window.location = url;
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

uploadEditor.resizeHeightWhenUploadOrRemove = function(input) {
    if ($(input).attr("class").includes("xava_upload ox-file") && $('.ox-element-collection').find('.filepond--root').length !== 0) {
        idSelector = "#" + input.id;
        classSelector = idSelector + " > .filepond--list-scroller";
        $(idSelector).removeClass("ox-element-collection-file ox-element-collection-image").addClass("ox-element-collection-add-new");
        $(classSelector).removeClass("ox-element-collection-image-scroll ox-element-collection-file-scroll");
        $(classSelector).css("height","100%");
    }
}

uploadEditor.resizeHeightWhenAddOrProcess = function(input) {
    if ($(input).attr("class").includes("xava_upload ox-file") && $('.ox-element-collection').find('.filepond--root').length !== 0) {
        idSelector = "#" + input.id;
        classSelector = idSelector + " > .filepond--list-scroller";
        var splittedFileName = $(idSelector).find('legend').text().split(".");
        if (splittedFileName.length > 1 && (uploadEditor.imgList.includes(splittedFileName[splittedFileName.length - 1].toUpperCase()))) {
            $(idSelector).removeClass("ox-element-collection-add-new");
            $(classSelector).css("height", "");
            $(classSelector).addClass("ox-element-collection-image-scroll");
        } else {
            $(idSelector).removeClass("ox-element-collection-add-new").addClass("ox-element-collection-file");
            $(classSelector).addClass("ox-element-collection-file-scroll");
        }
    }
}

uploadEditor.fileValidateTypeLabelExpectedTypesMap = {
    '.csv': null,
    'text/csv': 'CSV',
	'text/html': 'HTML, HTM, SHTML',
	'text/css': 'CSS',
	'text/xml': 'XML',
	'text/plain': 'TXT',
	'text/vnd.sun.j2me.app-descriptor': 'JAD',
	'text/vnd.wap.wml': 'WML',
	'text/x-component': 'HTC',
	'text/mathml': 'MML',	
	'image/*': 'PNG, JPG, GIF, TIF, BMP...',
	'image/gif': 'GIF',
	'image/jpeg': 'JPEG, JPG',
	'image/png': 'PNG',
	'image/tiff': 'TIF, TIFF',
	'image/vnd.wap.wbmp': 'WBMP',
	'image/x-icon': 'ICO',
	'image/x-jng': 'JNG',
	'image/x-ms-bmp': 'BMP',
	'image/svg+xml': 'SVG',
	'image/webp': 'WEBP',
	'application/x-javascript': 'JS',
	'application/atom+xml': 'ATOM',
	'application/rss+xml': 'RSS',
	'application/java-archive': 'JAR, WAR, EAR',
	'application/mac-binhex40': 'HQX',
	'application/msword': 'DOC',
	'application/pdf': 'PDF',
	'application/postscript': 'PS, EPS, AI',
	'application/rtf': 'RTF',
	'application/vnd.ms-excel': 'XLS',
	'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'XLSX',
	'application/vnd.ms-powerpoint': 'PPT',
	'application/vnd.wap.wmlc': 'WMLC',
	'application/vnd.google-earth.kml+xml': 'KML',
	'application/vnd.google-earth.kmz': 'KMZ',
	'application/x-7z-compressed': '7z',
	'application/x-cocoa': 'CCO',
	'application/x-java-archive-diff': 'JARDIFF',
	'application/x-java-jnlp-file': 'JNLP',
	'application/x-makeself': 'RUN',
	'application/x-perl': 'PL, PM',
	'application/x-pilot': 'PRC, PDB',
	'application/x-rar-compressed': 'RAR',
	'application/x-redhat-package-manager': 'RPM',
	'application/x-sea': 'SEA',
	'application/x-shockwave-flash': 'SWF',
	'application/x-stuffit': 'SIT',
	'application/x-tcl': 'TCL, TK',
	'application/x-x509-ca-cert': 'DER, PEM, CRT',
	'application/x-xpinstall': 'XPI',
	'application/xhtml+xml': 'XHTML',
	'application/zip': 'ZIP',
	'audio/midi': 'MID, MIDI, KAR',
	'audio/mpeg': 'MP3',
	'audio/ogg': 'OGG',
	'audio/x-realaudio': 'RA',
	'video/3gpp': '3GPP, 3GP',
	'video/mpeg': 'MPEG, MPG',
	'video/quicktime': 'MOV',
	'video/x-flv': 'FLV',
	'video/x-mng': 'MNG',
	'video/x-ms-asf': 'ASX, ASF',
	'video/x-ms-wmv': 'WMV',
	'video/x-msvideo': 'AVI',
	'video/mp4': 'M4V, MP4'		        
}

uploadEditor.imgList = ['GIF','JPEG','JPG','PNG','TIF','TIFF','WBMP','ICO','JNG','BMP','SVG','WEBP'];


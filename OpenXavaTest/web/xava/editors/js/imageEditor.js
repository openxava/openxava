/* tmp ¿ Generalizar para todos los files ? */

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);
    
    // tmp ¿Funciona con más de una PHOTO en la vista?
	
    const inputs = document.querySelectorAll('.xava_image');
    
    inputs.forEach(function(input) {
    	if (FilePond.find(input) == null) {
	    	const pond = FilePond.create(input);
	    	/* tmp
	    	if (input.dataset.url !== "") {
	    		pond.onaddfile = null;
	    		pond.addFile(input.dataset.url);
	    	}
	    	else {
				pond.onaddfile = function() {
	    			console.log("File added");
	    		}	    		    		
	    	}
	    	pond.onremovefile = function() {
				pond.onaddfile = function() {
	    			console.log("File added");
	    		}	    		    		
    		}
    		*/
	    	// tmp ini
	    	if (input.dataset.url !== "") {
	    		pond.addFile(input.dataset.url);
	    	}
	    	else {
		    	pond.setOptions({ // tmp Duplicado, refactorizar
		    	    server: {
		    	    	process: '../xava/upload?application=' + input.dataset.application + "&module=" + input.dataset.module, 
		    	        fetch: null,
		    	        revert: null
		    	    }
		    	});
	    	}
	    	pond.onremovefile = function() {
		    	pond.setOptions({
		    	    server: {
		    	        process: '../xava/upload?application=' + input.dataset.application + "&module=" + input.dataset.module, 
		    	        fetch: null,
		    	        revert: null
		    	    }
		    	});
    		}	    	
	    	// tmp fin
    	}
 
    });
	
});



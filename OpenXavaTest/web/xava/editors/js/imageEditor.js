/* tmp ¿ Generalizar para todos los files */

openxava.addEditorInitFunction(function() {

    FilePond.registerPlugin(FilePondPluginImagePreview);
	
    const inputElement = document.querySelector('input[type="file"]');
	
	const pond = FilePond.create( inputElement, {
		allowReplace: true
	});
	
});

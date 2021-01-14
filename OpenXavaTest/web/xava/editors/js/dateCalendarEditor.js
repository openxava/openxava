/* tmp */
openxava.addEditorInitFunction(function() {
	console.log("[openxava.addEditorInitFunction] CALENDAR INIT"); // tmp
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false, 
	    wrap: true,
	    onChange: function(selectedDates, dateStr, instance) {
        	console.log("[openxava.onChange] "); // tmp
    	},
    	onValueUpdate: function(selectedDates, dateStr, instance) {
        	console.log("[openxava.onValueUpdate] "); // tmp
    	},
    	 
	});
});


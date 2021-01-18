/* tmp */
openxava.addEditorInitFunction(function() {
	// tmp flatpickr.localize(flatpickr.l10ns.ru);
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,  
	    wrap: true,
	    locale: fp.l10ns.ru,
	    onChange: function(selectedDates, dateStr, instance) {
	    	/* tmp
	    	console.log("[dateCalendarEditor.onChange] >>>>>>>>>"); // tmp
	    	console.log("[dateCalendarEditor.onChange] dateStr=" + dateStr); // tmp
	    	console.log("[dateCalendarEditor.onChange] $(instance.input).attr('value')=" + $(instance.input).attr('value')); // tmp
	    	*/	    	
        	if (!$(instance.input).data("datePopupJustClosed") || dateStr === $(instance.input).attr('value')) {
        		$(instance.input).data("changedCancelled", true);
        	}
        	$(instance.input).attr('value', dateStr);
        	$(instance.input).removeData("datePopupJustClosed");
    	},
    	onClose: function(selectedDates, dateStr, instance) {
	    	$(instance.input).data("datePopupJustClosed", true);
    	},    	 
	});
});


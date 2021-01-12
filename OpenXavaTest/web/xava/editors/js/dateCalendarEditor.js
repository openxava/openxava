/* tmp */
if (dateCalendarEditor == null) var dateCalendarEditor = {};

openxava.addEditorInitFunction(function() {
	$('input.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,
	});
});

dateCalendarEditor.show = function(id) {
	flatpickr($('#' + id)).open();
}


openxava.addEditorInitFunction(function() {
	$('.corporation-employee-list-select').off('change').change(function() {
		openxava.executeAction($(this).data('application'), $(this).data('module'), false, false, 
			'CorporationEmployee.filter', 'segment='+this.value);
	});
});

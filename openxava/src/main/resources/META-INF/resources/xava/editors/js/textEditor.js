openxava.addEditorInitFunction(function() {
	var defaultValues = [];
	$("input[data-inputmask]").each(function() {
		defaultValues.push($(this).val());
	});
	
    $('input.xava_numeric').autoNumeric();
    $("input[data-inputmask]").inputmask();
	
    Inputmask.extendDefaults({
        'placeholder': ""
    });
    Inputmask.extendDefinitions({
        'L': {
            validator: "[A-Za-z]",
            casing: null
        },
        '0': {
            validator: "[0-9]"
        },
        'A': {
            validator: "[0-9a-zA-Z]",
            casing: null
        },
        '#': {
            validator: "[0-9 +-]"
        },
		'*': {
			validator: "[0-9A-Za-z!#$%&'*+/=?^_`{|}~\-]",
			casing: "lower"
		}
    });
	
	$("input[data-inputmask]").each(function(index) {
		$(this).val(defaultValues[index]);
	});
});
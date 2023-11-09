if (editableValidValuesEditor == null) var editableValidValuesEditor = {};

// tmr ini
openxava.addEditorInitFunction(function() {
	$('.ox-select-editable select').change(function() {
		editableValidValuesEditor.handleSelectChange(this);
	});
	$('.ox-select-editable input[type="text"]').change(function() {
		editableValidValuesEditor.handleSelectInput(this)
	});
});
// tmr fin

editableValidValuesEditor.handleSelectChange = function(selectElement) {
	selectElement.nextElementSibling.value = selectElement.options[selectElement.selectedIndex].text;
	selectElement.options[0].value = selectElement.options[selectElement.selectedIndex].value;
	selectElement.options[0].innerHTML = selectElement.options[selectElement.selectedIndex].text;
}

editableValidValuesEditor.handleSelectInput = function(inputElement) {
	inputElement.previousElementSibling.selectedIndex = 0;
	inputElement.previousElementSibling.options[0].value = inputElement.value;
	inputElement.previousElementSibling.options[0].innerHTML = inputElement.value;
}
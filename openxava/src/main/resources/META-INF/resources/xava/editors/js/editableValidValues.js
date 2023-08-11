    function updateInputValue(selectElement) {
        var selectedText = selectElement.options[selectElement.selectedIndex].text;
        var inputElement = selectElement.nextElementSibling;
        inputElement.value = selectedText;
    }
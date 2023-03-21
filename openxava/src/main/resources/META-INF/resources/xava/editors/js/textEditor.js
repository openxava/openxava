openxava.addEditorInitFunction(function() {

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
        }
    });

});
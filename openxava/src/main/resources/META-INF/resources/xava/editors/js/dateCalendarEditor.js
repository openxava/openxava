// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;

    var withEnter = false;
    var dateEnter;
	var onOpenDateTime;

	$('.xava_date > input').keydown(function(event) {
    var keycode = event.keyCode || event.which;	
    if(keycode == 13) {
        dateEnter = $(this).val();
		if (dateEnter.includes("/") || dateEnter.includes(".") || dateEnter.includes("-") || !(dateEnter.length < 9)){
			withEnter = false;
		}else{
			withEnter = true;
		}
    }
	});
	$('.xava_date > input').change(function() { 
        console.log(".xava_date > input");
		var dateFormat = $(this).parent().data("dateFormat");
  		if (withEnter == true){
			var date =dateEnter;
		}else{
			var date = $(this).val();
		}
		if (date === "") return;
		date = date.trim(); 
		var separator = dateFormat.substr(1, 1); 
		var idx = date.lastIndexOf(separator);
		if (idx < 0) {
			if (date.length % 2 != 0) date = " " + date;
			var inc = dateFormat.substr(0, 1) === 'Y'?2:0;
			var last = date.substring(4 + inc);
			var middle = date.substring(2 + inc, 4 + inc);
			var first = date.substring(0, 2 + inc);
			date = first + separator + middle + separator + last;
			date = date.trim(); 			
		}	
		idx = date.lastIndexOf(separator);
		var idxSpace = date.indexOf(' ');
		var pureDate = date;
		var time = "";
		if (idxSpace >= 0) {
			time = date.substr(idxSpace);
			pureDate = date.substr(0, idxSpace);
		}
		var suffix = "";
		if (idx == pureDate.length - 1) {
			pureDate = pureDate.substring(0, idx);
			suffix = separator;
			idx = pureDate.lastIndexOf(separator);
		}
		if (dateFormat.indexOf('Y') > 0 && pureDate.length - idx < 4) { 
  			var dateNoYear = pureDate.substring(0, idx);
  			var year = pureDate.substring(idx + 1);	
  			var prefix = year > 50?"19":"20";
  			date = dateNoYear + separator + prefix + year + suffix + time; 
  		}			
  		$(this).val(date);
	});
	$('.flatpickr-calendar').remove();
	$('.xava_date').flatpickr({
	    allowInput: true,
	    clickOpens: false,  
	    wrap: true,
	    locale: openxava.language,
		onOpen : function(selectedDates, dateStr, instance){
			onOpenDateTime = dateStr;
        },
	    onChange: function(selectedDates, dateStr, instance) {
			//console.log(".xava_date > onChange " + $(instance.input).data("datePopupJustClosed"));
			/*
        	if (!$(instance.input).data("datePopupJustClosed") || dateStr === $(instance.input).attr('value')) {
				console.log(".xava_date > onChange " + $(instance.input).attr('value'));
        		$(instance.input).data("changedCancelled", true);
        	}*/
			console.log(onOpenDateTime.length);
			if (onOpenDateTime != null && onOpenDateTime.length > 10 ){
				console.log("mayor a 10")
				// si entro al calendario
				if (onOpenDateTime == dateStr){
				console.log("onChange igual");
				$(instance.input).data("changedCancelled", true);
				}else{
				console.log("onChange no igual");
				}
				
			}else{
				//si no entro al calendario
				//si mi fecha actual es distinto al input no pasa nada, de lo contrario lo actualiza
				var st = (dateStr === $(instance.input).attr('value'))?"iguald":"noiguald";
				console.log(st);
				
				if (st == "iguald"){
					$(instance.input).data("changedCancelled", true);
				}
				//$(instance.input).attr('value', dateStr);
			}

        	$(instance.input).attr('value', dateStr);
        	//$(instance.input).removeData("datePopupJustClosed");
    	},

    	onClose: function(selectedDates, dateStr, instance) {
			/*
			if ( onOpenDateTime == dateStr){
				console.log("onClose igual");
				$(instance.input).data("changedCancelled", true);
			}else{
				$(instance.input).data("changedCancelled", false);
				console.log("onClose no es igual");
			}
			$(instance.input).attr('value', dateStr);*/
            //console.log(".xava_date > onClose" + dateStr);
	    	//$(instance.input).data("datePopupJustClosed", false);
    	},    	 
	});
});

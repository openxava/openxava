// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt

openxava.getScript(openxava.contextPath + "/xava/editors/flatpickr/" + openxava.language + ".js");

openxava.addEditorInitFunction(function() {
	if (openxava.browser.htmlUnit) return;

    var withEnter = false;
    var enterDate;
	var onOpenDateTime;

	$('.xava_date > input').keydown(function(event) {
	    var keycode = event.keyCode || event.which;	
	    if(keycode == 13) {
	        enterDate = $(this).val();
			if ((enterDate.includes("/") || enterDate.includes(".") || enterDate.includes("-")) && enterDate.length > 9){
				withEnter = false;
			}else{
				withEnter = true;
			}
	    }
	});
	$('.xava_date > input').change(function() { 
        console.log(".xava_date > input" + enterDate);
		var dateFormat = $(this).parent().data("dateFormat");
		var date = withEnter?enterDate:$(this).val();
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
		console.log("date " + date);
		date = date.includes(".20 ")? date.replace(".20 "," "): date;
  		$(this).val(date);
        enterDate = undefined;
		withEnter = false;
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
			/*
			if (!$(instance.input).data("datePopupJustClosed") || dateStr === $(instance.input).attr('value')) {
        		$(instance.input).data("changedCancelled", true);
        	}
        	$(instance.input).attr('value', dateStr);
        	$(instance.input).removeData("datePopupJustClosed");*/

			
			console.log("onChange onOpenDateTime " + onOpenDateTime);
			console.log("onChange dateStr " + dateStr);
            console.log("onChange $(instance.input).attr('value') " + $(instance.input).attr('value'));
            console.log("onChange enterDate " + enterDate);
			//si onOpenDateTime tiene valor, es porque estoy abriendo el calendario
			if (onOpenDateTime != null){
                console.log("calendar");
				if (onOpenDateTime.length > 10 ){
					//con hora, no hago el cambio, dejo que onClose lo defina
                    $(instance.input).data("changedCancelled", true);
                    /*
					console.log("calendario onOpen > 10");
					if (onOpenDateTime == dateStr){
						// no cambiar si es igual
						console.log("onChange igual");
						$(instance.input).data("changedCancelled", true);
					}else{
						// no cambiar si es que el usuario quiere cambiar mas datos
						// si pongo que cambie, actuara como el calendario normal, onchange siempre si se cambia solo 1 dato
						console.log("onChange no igual");
						$(instance.input).data("changedCancelled", true);
					}*/
				 }else{
					//sin hora
					console.log("calendario onOpen sin horario");
					console.log("onOpenDateTime" + onOpenDateTime);
					console.log("dateStr" + dateStr);
					if (onOpenDateTime == dateStr){
						// no cambiar si es igual
						console.log("onChange igual");
						$(instance.input).data("changedCancelled", true);
					}else{
						// no cambiar si es que el usuario quiere cambiar mas datos
						// si pongo que cambie, actuara como el calendario normal, onchange siempre si se cambia solo 1 dato
                        console.log("se ejecuta onChange");
                        $(instance.input).attr('value', dateStr);
					}
				}
			}else{
				//no entro al calendario, uso el input
				console.log("input");
				if (dateStr === $(instance.input).attr('value')){
					//es igual, no hago el cambio
					console.log("es igual");
                    //$(instance.input).attr('value', dateStr);
					$(instance.input).data("changedCancelled", true);
				}else{
					console.log("es distinto");
					//es distinto
					$(instance.input).attr('value', dateStr);
				}
				//$(instance.input).attr('value', dateStr);
			}
    	},

    	onClose: function(selectedDates, dateStr, instance) {
            console.log("onClose dateStr" + dateStr);
			if (onOpenDateTime != null){
				if ( onOpenDateTime == dateStr){
				console.log("onClose igual");
				$(instance.input).data("changedCancelled", true);
			}else{
				//$(instance.input).data("changedCancelled", false);
				console.log("onClose no es igual");
				$(instance.input).attr('value', dateStr);
				$("#ox_openxavatest_Appointment__time").change();
                
                console.log("onClose " + $(instance.input).attr('value'));
                //abro calendario, selecciono > onClose > onChange > no encuentra opendatetime si dejo undefined
				//onOpenDateTime = undefined;
			}
			}
			/*	
			$(instance.input).attr('value', dateStr);
            console.log(".xava_date > onClose" + dateStr);
	    	$(instance.input).data("datePopupJustClosed", false);*/
    	},    	 
	});
});

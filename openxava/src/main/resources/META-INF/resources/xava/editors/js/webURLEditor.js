openxava.addEditorInitFunction(function() {
	$('.ox-web-url a').off('click').click(function() {
		var url=$(this).parent().find("input").val();
		if (url.substr(0, 7) !== 'http://' && url.substr(0, 8) !== 'https://') url = 'http://' + url; 
		this.href=url;
	});
});


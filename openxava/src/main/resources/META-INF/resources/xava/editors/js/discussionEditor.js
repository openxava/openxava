if (discussionEditor == null) var discussionEditor = {};

openxava.addEditorInitFunction(function() {
	$('.ox-discussion-add-button').off('click').click(function() {
		discussionEditor.postMessage(openxava.lastApplication, openxava.lastModule, $(this).parent().data("discussion-id"))
	});
	$('.ox-discussion-cancel-button').off('click').click(function() {
		discussionEditor.cancel($(this).parent().data("discussion-id"));
	});	
});

discussionEditor.postMessage = function(application, module, discussionId) {
	var newComment = tinymce.get('xava_new_comment_' + discussionId); 
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	var commentContent = newComment.getContent(); 
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.slideDown(); 
	newComment.resetContent(""); 

	discussionEditor.sendComment(application, module, discussionId, commentContent);

	comments.append(template);
	discussionEditor.clear(discussionId); 
}

discussionEditor.postMessageHtmlUnit = function(application, module, discussionId, commentContent) {
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.show(); 

	discussionEditor.sendComment(application, module, discussionId, commentContent);

	comments.append(template);
}

discussionEditor.sendComment = function(application, module, discussionId, commentContent) {
	var url = openxava.contextPath + "/xava/discussion";
	var params = new URLSearchParams();
	params.append("application", application);
	params.append("module", module);
	params.append("discussionId", discussionId);
	params.append("commentContent", commentContent);

	fetch(url, {
		method: "POST",
		credentials: "same-origin",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"xavawindowid": $("#xava_window_id").val()
		},
		body: params
	})
	.then(function(response) {
		if (!response.ok) {
			throw new Error("HTTP status " + response.status);
		}
	})
	.catch(function(error) {
		console.error("Error posting discussion comment:", error);
		alert("Error: Discussion comment not added");
	});
}

discussionEditor.cancel = function(discussionId) {
	discussionEditor.clear(discussionId);
}

discussionEditor.clear = function(discussionId) {
	$("#xava_new_comment_" + discussionId + "_buttons input").fadeOut();
	$('.ox-button-bar-button').fadeIn();
	$('.ox-bottom-buttons').css("visibility", "visible");  
	$('.ox-bottom-buttons').children().fadeIn(); 
	tinymce.get('xava_new_comment_' + discussionId).resetContent(); 
}

if (discussionEditor == null) var discussionEditor = {};

discussionEditor.postMessage = function(application, module, discussionId) {
	// tmr var newComment = $('#xava_new_comment_' + discussionId);
	var newComment = tinymce.get('xava_new_comment_' + discussionId); // tmr
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	// tmr var commentContent = newComment.val();
	var commentContent = newComment.getContent(); // tmr
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.slideDown(); 
	// tmr newComment.val("");
	newComment.resetContent(""); // tmr
	Discussion.postComment(application, module, discussionId, commentContent);
	comments.append(template);
	discussionEditor.clear(discussionId); 
}

discussionEditor.postMessageHtmlUnit = function(application, module, discussionId, commentContent) {
	console.log("[discussionEditor.postMessageHtmlUnit] 10"); // tmr
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.show(); 
	console.log("[discussionEditor.postMessageHtmlUnit] PRE"); // tmr
	Discussion.postComment(application, module, discussionId, commentContent);
	console.log("[discussionEditor.postMessageHtmlUnit] POST"); // tmr
	comments.append(template);
	console.log("[discussionEditor.postMessageHtmlUnit] 999"); // tmr 
}

discussionEditor.cancel = function(discussionId) {
	discussionEditor.clear(discussionId);
}

discussionEditor.clear = function(discussionId) {
	$("#xava_new_comment_" + discussionId + "_buttons input").fadeOut();
	$('.ox-button-bar-button').fadeIn(); 
	$('.ox-bottom-buttons').children().fadeIn(); 
	// tmr CKEDITOR.instances['xava_new_comment_' + discussionId].setData('');
	tinymce.get('xava_new_comment_' + discussionId).resetContent(); // tmr
}

if (discussionEditor == null) var discussionEditor = {};

discussionEditor.postMessage = function(application, module, discussionId) {
	var newComment = tinymce.get('xava_new_comment_' + discussionId); 
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	var commentContent = newComment.getContent(); 
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.slideDown(); 
	newComment.resetContent(""); 
	Discussion.postComment(application, module, discussionId, commentContent);
	comments.append(template);
	discussionEditor.clear(discussionId); 
}

discussionEditor.postMessageHtmlUnit = function(application, module, discussionId, commentContent) {
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.show(); 
	Discussion.postComment(application, module, discussionId, commentContent);
	comments.append(template);
}

discussionEditor.cancel = function(discussionId) {
	discussionEditor.clear(discussionId);
}

discussionEditor.clear = function(discussionId) {
	$("#xava_new_comment_" + discussionId + "_buttons input").fadeOut();
	$('.ox-button-bar-button').fadeIn(); 
	$('.ox-bottom-buttons').children().fadeIn(); 
	tinymce.get('xava_new_comment_' + discussionId).resetContent(); 
}

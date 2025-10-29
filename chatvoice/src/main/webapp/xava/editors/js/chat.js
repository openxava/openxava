if (chatEditor == null) var chatEditor = {};

openxava.addEditorInitFunction(function() {
	var chatMessages = $('#chatMessages');
	var chatInput = $('#chatInput');
	var chatSendBtn = $('#chatSendBtn');
	
	if (chatMessages.length === 0 || chatInput.length === 0 || chatSendBtn.length === 0) {
		return;
	}
	
	chatInput.off('input keyup').on('input keyup', function() {
		chatEditor.autoResizeTextarea(chatInput);
		chatEditor.updateSendButton(chatInput, chatSendBtn);
	});
	
	chatInput.off('keypress').on('keypress', function(e) {
		if (e.key === 'Enter' && !e.shiftKey) {
			e.preventDefault();
			var text = chatInput.val().trim();
			if (text) {
				chatEditor.sendMessage(chatMessages, chatInput, chatSendBtn, text);
			}
		}
	});
	
	chatSendBtn.off('click').on('click', function(e) {
		e.preventDefault();
		var text = chatInput.val().trim();
		if (text) {
			chatEditor.sendMessage(chatMessages, chatInput, chatSendBtn, text);
		}
	});
	
	chatEditor.updateSendButton(chatInput, chatSendBtn);
	chatInput.focus();
});

chatEditor.autoResizeTextarea = function(input) {
	input.css('height', 'auto');
	var newHeight = Math.min(input[0].scrollHeight, 200);
	input.css('height', newHeight + 'px');
};

chatEditor.updateSendButton = function(input, button) {
	var hasText = input.val().trim().length > 0;
	button.prop('disabled', !hasText);
};

chatEditor.hideWelcome = function(container) {
	$('.ox-chat-welcome').hide();
};

chatEditor.createMessage = function(text, isUser) {
	var messageDiv = $('<div>').addClass('ox-chat-message').addClass(isUser ? 'user' : 'assistant');
	var content = $('<div>').addClass('ox-chat-message-content');
	var p = $('<p>').text(text);
	
	content.append(p);
	messageDiv.append(content);
	
	return messageDiv;
};

chatEditor.createTypingIndicator = function() {
	var messageDiv = $('<div>').addClass('ox-chat-message assistant').attr('id', 'typingIndicator');
	var content = $('<div>').addClass('ox-chat-message-content');
	var typing = $('<div>').addClass('ox-chat-typing').html('<span></span><span></span><span></span>');
	
	content.append(typing);
	messageDiv.append(content);
	
	return messageDiv;
};

chatEditor.scrollToBottom = function(container) {
	container.scrollTop(container[0].scrollHeight);
};

chatEditor.sendMessage = function(container, input, button, text) {
	chatEditor.hideWelcome(container);
	$('.ox-chat-center-content').addClass('has-messages');
	
	var userMessage = chatEditor.createMessage(text, true);
	container.append(userMessage);
	
	input.val('');
	input.css('height', 'auto');
	chatEditor.updateSendButton(input, button);
	
	chatEditor.scrollToBottom(container);
	
	var typingIndicator = chatEditor.createTypingIndicator();
	container.append(typingIndicator);
	chatEditor.scrollToBottom(container);
	
	setTimeout(function() {
		$('#typingIndicator').remove();
		
		var assistantMessage = chatEditor.createMessage(text, false);
		container.append(assistantMessage);
		chatEditor.scrollToBottom(container);
	}, 500 + Math.random() * 1000);
};

if (chat == null) var chat = {};

chat.STORAGE_KEY = 'oxChatMessages';

chat.init = function() {
	var chatMessages = $('#chatMessages');
	var chatInput = $('#chatInput');
	var chatSendBtn = $('#chatSendBtn');
	var chatNewConversationBtn = $('#chatNewConversationBtn');
	
	if (chatMessages.length === 0 || chatInput.length === 0 || chatSendBtn.length === 0) {
		return;
	}
	
	chat.restoreMessages(chatMessages);
	
	chatInput.off('input keyup').on('input keyup', function() {
		chat.autoResizeTextarea(chatInput);
		chat.updateSendButton(chatInput, chatSendBtn);
	});
	
	chatInput.off('keypress').on('keypress', function(e) {
		if (e.key === 'Enter' && !e.shiftKey) {
			e.preventDefault();
			var text = chatInput.val().trim();
			if (text) {
				chat.sendMessage(chatMessages, chatInput, chatSendBtn, text);
			}
		}
	});
	
	chatSendBtn.off('click').on('click', function(e) {
		e.preventDefault();
		var text = chatInput.val().trim();
		if (text) {
			chat.sendMessage(chatMessages, chatInput, chatSendBtn, text);
		}
	});
	
	chatNewConversationBtn.off('click').on('click', function(e) {
		e.preventDefault();
		chat.newConversation(chatMessages, chatInput);
	});
	
	chat.updateSendButton(chatInput, chatSendBtn);
};

if (typeof openxava !== 'undefined' && openxava.addEditorInitFunction) {
	openxava.addEditorInitFunction(function() {
		chat.init();
		$('#chatInput').focus();
	});
}

chat.autoResizeTextarea = function(input) {
	input.css('height', 'auto');
	var newHeight = Math.min(input[0].scrollHeight, 200);
	input.css('height', newHeight + 'px');
};

chat.updateSendButton = function(input, button) {
	var hasText = input.val().trim().length > 0;
	button.prop('disabled', !hasText);
};

chat.hideWelcome = function(container) {
	$('.ox-chat-welcome').hide();
	$('.ox-chat-header').removeClass('hidden');
};

chat.showWelcome = function() {
	$('.ox-chat-welcome').show();
	$('.ox-chat-header').addClass('hidden');
};

chat.createMessage = function(text, isUser) {
	var messageDiv = $('<div>').addClass('ox-chat-message').addClass(isUser ? 'user' : 'assistant');
	var content = $('<div>').addClass('ox-chat-message-content');
	
	if (isUser) {
		// User messages as plain text
		var p = $('<p>').text(text);
		content.append(p);
	} else {
		// Assistant messages with HTML already processed from the server
		content.html(text);
	}
	
	messageDiv.append(content);
	
	return messageDiv;
};

chat.createTypingIndicator = function() {
	var messageDiv = $('<div>').addClass('ox-chat-message assistant').attr('id', 'typingIndicator');
	var content = $('<div>').addClass('ox-chat-message-content');
	var typing = $('<div>').addClass('ox-chat-typing').html('<span></span><span></span><span></span>');
	
	content.append(typing);
	messageDiv.append(content);
	
	return messageDiv;
};

chat.scrollToBottom = function(container) {
	container.scrollTop(container[0].scrollHeight);
};

chat.saveMessages = function() {
	var messages = [];
	$('#chatMessages .ox-chat-message').each(function() {
		var $msg = $(this);
		var isUser = $msg.hasClass('user');
		var content = $msg.find('.ox-chat-message-content').html();
		messages.push({ isUser: isUser, content: content });
	});
	sessionStorage.setItem(chat.STORAGE_KEY, JSON.stringify(messages));
};

chat.restoreMessages = function(container) {
	var stored = sessionStorage.getItem(chat.STORAGE_KEY);
	if (!stored) return;
	
	try {
		var messages = JSON.parse(stored);
		if (messages.length === 0) return;
		
		container.empty();
		
		chat.hideWelcome(container);
		$('.ox-chat-center-content').addClass('has-messages');
		
		messages.forEach(function(msg) {
			var messageDiv = $('<div>').addClass('ox-chat-message').addClass(msg.isUser ? 'user' : 'assistant');
			var content = $('<div>').addClass('ox-chat-message-content').html(msg.content);
			messageDiv.append(content);
			container.append(messageDiv);
		});
		
		chat.scrollToBottom(container);
	} catch (e) {
		console.error('Error restoring chat messages:', e);
	}
};

chat.sendMessage = function(container, input, button, text) {
	chat.hideWelcome(container);
	$('.ox-chat-center-content').addClass('has-messages');
	
	var userMessage = chat.createMessage(text, true);
	container.append(userMessage);
	chat.saveMessages();
	
	input.val('');
	input.css('height', 'auto');
	chat.updateSendButton(input, button);
	
	chat.scrollToBottom(container);
	
	var typingIndicator = chat.createTypingIndicator();
	container.append(typingIndicator);
	chat.scrollToBottom(container);
	
	// Send message via WebSocket
	chat.sendViaWebSocket(text);
};

chat.getWebSocket = function() {
	if (!chat.ws || chat.ws.readyState === WebSocket.CLOSED) {
		var protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
		var wsUrl = protocol + '//' + window.location.host + openxava.contextPath + '/chat-ws';
		
		chat.ws = new WebSocket(wsUrl);
		
		chat.ws.onopen = function() {
			console.log('WebSocket connected');
		};
		
		chat.ws.onmessage = function(event) {
			// Check for special refresh UI command
			if (event.data === '__REFRESH_UI__') {
				var action = openxava.isListMode() ? 'List.filter' : 'CRUD.refresh';
				openxava.executeAction(openxava.lastApplication, openxava.lastModule, '', false, action);
				return;
			}
			
			// Check for filter list command: __FILTER_LIST__:{"conditionValue___0":"value",...}
			if (event.data.startsWith('__FILTER_LIST__:')) {
				var json = event.data.substring('__FILTER_LIST__:'.length);
				var filterValues = JSON.parse(json);
				openxava.filterList(filterValues);
				return;
			}
			
			$('#typingIndicator').remove();
			
			var assistantMessage = chat.createMessage(event.data, false);
			$('#chatMessages').append(assistantMessage);
			chat.scrollToBottom($('#chatMessages'));
			chat.saveMessages();
		};
		
		chat.ws.onerror = function(error) {
			console.error('WebSocket error:', error);
			$('#typingIndicator').remove();
			alert('Error connecting to chat service');
		};
		
		chat.ws.onclose = function() {
			console.log('WebSocket closed');
		};
	}
	return chat.ws;
};

chat.sendViaWebSocket = function(message) {
	var ws = chat.getWebSocket();
	
	if (ws.readyState === WebSocket.OPEN) {
		ws.send(message);
	} else if (ws.readyState === WebSocket.CONNECTING) {
		// Wait for connection to open
		ws.addEventListener('open', function() {
			ws.send(message);
		}, { once: true });
	} else {
		alert('Cannot send message: WebSocket is not connected');
	}
};

chat.newConversation = function(container, input) {
	// Clear all messages from UI
	container.empty();
	
	// Clear saved messages
	sessionStorage.removeItem(chat.STORAGE_KEY);
	
	// Show welcome message and hide header
	chat.showWelcome();
	$('.ox-chat-center-content').removeClass('has-messages');
	
	// Clear the input
	input.val('');
	input.css('height', 'auto');
	input.focus();
	
	// Send command to clear memory on server and close WebSocket
	if (chat.ws && chat.ws.readyState === WebSocket.OPEN) {
		chat.ws.send('__NEW_CONVERSATION__');
		chat.ws.close();
		chat.ws = null;
	}
};

chat.PANEL_STATE_KEY = 'oxChatPanelOpen';

chat.initPanel = function() {
	$('#chat_panel_hide').on("click", function() {
		chat.hidePanel();
	});
	$('#chat_panel_show, #module_header_chat_button').on("click", function() {
		chat.showPanel();
	});
	
	chat.restorePanelState();
	
	if (typeof chat !== 'undefined' && chat.init) {
		chat.init();
	}
}

chat.restorePanelState = function() {
	var savedState = sessionStorage.getItem(chat.PANEL_STATE_KEY);
	if (savedState === null) return;
	
	var shouldBeOpen = savedState === 'true';
	var isCurrentlyVisible = $('#chat_panel').is(':visible');
	
	if (shouldBeOpen && !isCurrentlyVisible) {
		$('#chat_panel_show').hide();
		$('#module_header_chat_button').hide();
		$('.module-wrapper').css('margin-right', '330px');
		$('#chat_panel').show();
		$('#chat_panel_hide').show();
	} else if (!shouldBeOpen && isCurrentlyVisible) {
		$('#chat_panel_hide').hide();
		$('#module_header_chat_button').show();
		$('.module-wrapper').css('margin-right', '0');
		$('#chat_panel').hide();
		$('#chat_panel_show').show();
	}
}

chat.hidePanel = function() {
	sessionStorage.setItem(chat.PANEL_STATE_KEY, 'false');
	$('#chat_panel_hide').hide();
	$('#module_header_chat_button').show();
	$('.module-wrapper').css('margin-right', '0');
	$('#chat_panel').animate({width:'toggle'}, 200, function() {
		$('#chat_panel_show').fadeIn();
		openxava.resetListsSize(naviox.application, naviox.module);
	});
}

chat.showPanel = function() {
	sessionStorage.setItem(chat.PANEL_STATE_KEY, 'true');
	$('#chat_panel_show').hide();
	$('#module_header_chat_button').hide();
	$('.module-wrapper').css('margin-right', '330px');
	$('#chat_panel').animate({width:'toggle'}, 200, function() {
		$('#chat_panel_hide').fadeIn();
		openxava.resetListsSize(naviox.application, naviox.module);
	});
}

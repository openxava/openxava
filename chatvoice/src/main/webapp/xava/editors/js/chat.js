if (chatEditor == null) var chatEditor = {};

chatEditor.STORAGE_KEY = 'oxChatMessages';

chatEditor.init = function() {
	var chatMessages = $('#chatMessages');
	var chatInput = $('#chatInput');
	var chatSendBtn = $('#chatSendBtn');
	var chatNewConversationBtn = $('#chatNewConversationBtn');
	
	if (chatMessages.length === 0 || chatInput.length === 0 || chatSendBtn.length === 0) {
		return;
	}
	
	chatEditor.restoreMessages(chatMessages);
	
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
	
	chatNewConversationBtn.off('click').on('click', function(e) {
		e.preventDefault();
		chatEditor.newConversation(chatMessages, chatInput);
	});
	
	chatEditor.updateSendButton(chatInput, chatSendBtn);
};

if (typeof openxava !== 'undefined' && openxava.addEditorInitFunction) {
	openxava.addEditorInitFunction(function() {
		chatEditor.init();
		$('#chatInput').focus();
	});
}

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
	$('.ox-chat-header').removeClass('hidden');
};

chatEditor.showWelcome = function() {
	$('.ox-chat-welcome').show();
	$('.ox-chat-header').addClass('hidden');
};

chatEditor.createMessage = function(text, isUser) {
	var messageDiv = $('<div>').addClass('ox-chat-message').addClass(isUser ? 'user' : 'assistant');
	var content = $('<div>').addClass('ox-chat-message-content');
	
	if (isUser) {
		// Mensajes del usuario como texto plano
		var p = $('<p>').text(text);
		content.append(p);
	} else {
		// Mensajes del asistente con HTML ya procesado desde el servidor
		content.html(text);
	}
	
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

chatEditor.saveMessages = function() {
	var messages = [];
	$('#chatMessages .ox-chat-message').each(function() {
		var $msg = $(this);
		var isUser = $msg.hasClass('user');
		var content = $msg.find('.ox-chat-message-content').html();
		messages.push({ isUser: isUser, content: content });
	});
	sessionStorage.setItem(chatEditor.STORAGE_KEY, JSON.stringify(messages));
};

chatEditor.restoreMessages = function(container) {
	var stored = sessionStorage.getItem(chatEditor.STORAGE_KEY);
	if (!stored) return;
	
	try {
		var messages = JSON.parse(stored);
		if (messages.length === 0) return;
		
		container.empty();
		
		chatEditor.hideWelcome(container);
		$('.ox-chat-center-content').addClass('has-messages');
		
		messages.forEach(function(msg) {
			var messageDiv = $('<div>').addClass('ox-chat-message').addClass(msg.isUser ? 'user' : 'assistant');
			var content = $('<div>').addClass('ox-chat-message-content').html(msg.content);
			messageDiv.append(content);
			container.append(messageDiv);
		});
		
		chatEditor.scrollToBottom(container);
	} catch (e) {
		console.error('Error restoring chat messages:', e);
	}
};

chatEditor.sendMessage = function(container, input, button, text) {
	chatEditor.hideWelcome(container);
	$('.ox-chat-center-content').addClass('has-messages');
	
	var userMessage = chatEditor.createMessage(text, true);
	container.append(userMessage);
	chatEditor.saveMessages();
	
	input.val('');
	input.css('height', 'auto');
	chatEditor.updateSendButton(input, button);
	
	chatEditor.scrollToBottom(container);
	
	var typingIndicator = chatEditor.createTypingIndicator();
	container.append(typingIndicator);
	chatEditor.scrollToBottom(container);
	
	// Send message via WebSocket
	chatEditor.sendViaWebSocket(text);
};

chatEditor.getWebSocket = function() {
	if (!chatEditor.ws || chatEditor.ws.readyState === WebSocket.CLOSED) {
		var protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
		var wsUrl = protocol + '//' + window.location.host + openxava.contextPath + '/chat-ws';
		
		chatEditor.ws = new WebSocket(wsUrl);
		
		chatEditor.ws.onopen = function() {
			console.log('WebSocket connected');
		};
		
		chatEditor.ws.onmessage = function(event) {
			// Check for special refresh UI command
			if (event.data === '__REFRESH_UI__') {
				var action = openxava.isListMode() ? 'List.filter' : 'CRUD.refresh';
				openxava.executeAction(openxava.lastApplication, openxava.lastModule, '', false, action);
				return;
			}
			
			$('#typingIndicator').remove();
			
			var assistantMessage = chatEditor.createMessage(event.data, false);
			$('#chatMessages').append(assistantMessage);
			chatEditor.scrollToBottom($('#chatMessages'));
			chatEditor.saveMessages();
		};
		
		chatEditor.ws.onerror = function(error) {
			console.error('WebSocket error:', error);
			$('#typingIndicator').remove();
			alert('Error connecting to chat service');
		};
		
		chatEditor.ws.onclose = function() {
			console.log('WebSocket closed');
		};
	}
	return chatEditor.ws;
};

chatEditor.sendViaWebSocket = function(message) {
	var ws = chatEditor.getWebSocket();
	
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

chatEditor.newConversation = function(container, input) {
	// Limpiar todos los mensajes del UI
	container.empty();
	
	// Limpiar mensajes guardados
	sessionStorage.removeItem(chatEditor.STORAGE_KEY);
	
	// Mostrar el mensaje de bienvenida y ocultar el header
	chatEditor.showWelcome();
	$('.ox-chat-center-content').removeClass('has-messages');
	
	// Limpiar el input
	input.val('');
	input.css('height', 'auto');
	input.focus();
	
	// Cerrar el WebSocket actual para forzar nueva sesión con memoria limpia
	if (chatEditor.ws && chatEditor.ws.readyState === WebSocket.OPEN) {
		chatEditor.ws.close();
		chatEditor.ws = null;
	}
};

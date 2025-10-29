openxava.addEditorInitFunction(function() {
    // Elementos del DOM
    const chatMessages = document.getElementById('chatMessages');
    const chatInput = document.getElementById('chatInput');
    const chatSendBtn = document.getElementById('chatSendBtn');
    
    // Verificar que los elementos existen
    if (!chatMessages || !chatInput || !chatSendBtn) {
        return; // Salir si no estamos en la página del chat
    }
    
    // Función para ajustar la altura del textarea automáticamente
    function autoResizeTextarea() {
        chatInput.style.height = 'auto';
        chatInput.style.height = Math.min(chatInput.scrollHeight, 200) + 'px';
    }
    
    // Función para habilitar/deshabilitar el botón de envío
    function updateSendButton() {
        const hasText = chatInput.value.trim().length > 0;
        chatSendBtn.disabled = !hasText;
        console.log('updateSendButton - hasText:', hasText, 'disabled:', !hasText);
    }
    
    // Función para ocultar el mensaje de bienvenida
    function hideWelcome() {
        const welcome = chatMessages.querySelector('.ox-chat-welcome');
        if (welcome) {
            welcome.style.display = 'none';
        }
    }
    
    // Función para crear un mensaje
    function createMessage(text, isUser) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'ox-chat-message ' + (isUser ? 'user' : 'assistant');
        
        const avatar = document.createElement('div');
        avatar.className = 'ox-chat-message-avatar';
        avatar.textContent = isUser ? 'U' : 'AI';
        
        const content = document.createElement('div');
        content.className = 'ox-chat-message-content';
        
        const p = document.createElement('p');
        p.textContent = text;
        content.appendChild(p);
        
        messageDiv.appendChild(avatar);
        messageDiv.appendChild(content);
        
        return messageDiv;
    }
    
    // Función para crear el indicador de escritura
    function createTypingIndicator() {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'ox-chat-message assistant';
        messageDiv.id = 'typingIndicator';
        
        const avatar = document.createElement('div');
        avatar.className = 'ox-chat-message-avatar';
        avatar.textContent = 'AI';
        
        const content = document.createElement('div');
        content.className = 'ox-chat-message-content';
        
        const typing = document.createElement('div');
        typing.className = 'ox-chat-typing';
        typing.innerHTML = '<span></span><span></span><span></span>';
        
        content.appendChild(typing);
        messageDiv.appendChild(avatar);
        messageDiv.appendChild(content);
        
        return messageDiv;
    }
    
    // Función para hacer scroll al final
    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
    
    // Función para enviar mensaje
    function sendMessage() {
        const text = chatInput.value.trim();
        console.log('sendMessage called with text:', text);
        if (!text) return;
        
        // Ocultar mensaje de bienvenida si existe
        hideWelcome();
        
        // Agregar mensaje del usuario
        const userMessage = createMessage(text, true);
        chatMessages.appendChild(userMessage);
        
        // Limpiar input
        chatInput.value = '';
        chatInput.style.height = 'auto';
        updateSendButton();
        
        // Scroll al final
        scrollToBottom();
        
        // Mostrar indicador de escritura
        const typingIndicator = createTypingIndicator();
        chatMessages.appendChild(typingIndicator);
        scrollToBottom();
        
        // Simular respuesta después de un breve delay (comportamiento de eco)
        setTimeout(function() {
            // Eliminar indicador de escritura
            const indicator = document.getElementById('typingIndicator');
            if (indicator) {
                indicator.remove();
            }
            
            // Agregar respuesta del asistente (eco del mensaje del usuario)
            const assistantMessage = createMessage(text, false);
            chatMessages.appendChild(assistantMessage);
            scrollToBottom();
        }, 500 + Math.random() * 1000); // Delay aleatorio entre 500ms y 1500ms
    }
    
    // Event listeners - escuchar múltiples eventos para asegurar compatibilidad
    function handleInputChange() {
        autoResizeTextarea();
        updateSendButton();
    }
    
    chatInput.addEventListener('input', handleInputChange);
    chatInput.addEventListener('keyup', handleInputChange);
    chatInput.addEventListener('change', handleInputChange);
    
    chatInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            // Usar setTimeout para asegurar que el valor esté actualizado
            setTimeout(function() {
                const text = chatInput.value.trim();
                console.log('Enter pressed, text:', text);
                if (text) {
                    sendMessage();
                }
            }, 0);
        }
    });
    
    chatSendBtn.addEventListener('click', function(e) {
        e.preventDefault();
        const text = chatInput.value.trim();
        if (text) {
            sendMessage();
        }
    });
    
    // Inicialización
    updateSendButton();
    chatInput.focus();
});

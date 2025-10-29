<%@ include file="../imports.jsp"%>

<div class="ox-chat-container">
    <div class="ox-chat-messages" id="chatMessages">
    </div>
    
    <div class="ox-chat-center-content">
        <div class="ox-chat-welcome">
            <h2><xava:message key="chat_welcome"/></h2>
        </div>
        
        <div class="ox-chat-input-container">
            <div class="ox-chat-input-wrapper">
                <textarea 
                    id="chatInput" 
                    class="ox-chat-input" 
                    placeholder="<xava:message key='chat_input_placeholder'/>"
                    rows="1"></textarea>
                <button id="chatSendBtn" class="ox-chat-send-btn" disabled>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <path d="M7 11L12 6L17 11M12 18V7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
            </div>
        </div>
    </div>
</div>

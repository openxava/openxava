<%--
Chat panel for OpenXava applications.
Copyright 2024 Javier Paniza Lucas

License: Apache License, Version 2.0.	
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

License: GNU Lesser General Public License (LGPL), version 2.1 or later.
See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
--%>

<%@ include file="../xava/imports.jsp"%>

<div id="chat_panel">
	<div id="chat_panel_content">
		<div class="ox-chat-container">
		    <div class="ox-chat-header ox-collection-list-actions hidden"> 
		        <span class="ox-button-bar-button">
		            <a id="chatNewConversationBtn" title="<xava:message key='chat_new_conversation'/>">
		                <i class="mdi mdi-chat-plus"></i>
		                <span class="ox-action-label"><xava:message key="chat_new_conversation"/></span>
		            </a>
		        </span>
		    </div>
		    
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
	</div>
</div>

<a id="chat_panel_hide">
	<i class="mdi mdi-chevron-right"></i>
</a>

<a id="chat_panel_show">
	<i class="mdi mdi-chevron-left"></i>
</a>

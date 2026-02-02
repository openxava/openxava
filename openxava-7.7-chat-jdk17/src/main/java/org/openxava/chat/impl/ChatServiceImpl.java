package org.openxava.chat.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.openxava.chat.ChatException;
import org.openxava.chat.IChatService;
import org.openxava.controller.ModuleContext;
import org.openxava.util.Is;
import org.openxava.util.XavaPreferences;

import com.openxava.naviox.impl.MetaModuleFactory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

/**
 * Implementation of IChatService using LangChain4j and OpenAI.
 * This implementation requires Java 17.
 * 
 * @author Javier Paniza
 * @since 7.7
 */
public class ChatServiceImpl implements IChatService {
	
	interface Assistant {
		@SystemMessage("""
			You are a helpful assistant for an OpenXava business application.
			
			IMPORTANT RULES:
			1. ALWAYS remember the user's original question throughout the conversation.
			2. Before loading data, THINK about what you really need. Don't load all entities - only the ones required to answer the question.
			3. For aggregation questions (max, min, sum, count, most, least), use findEntitiesByCondition with appropriate conditions instead of loading all data.
			4. Keep your responses concise and focused on answering the user's question.
			5. CRITICAL: When user asks to show/display/filter data, ALWAYS call getCurrentModule() first to check which module they are viewing. If they are viewing the SAME entity they ask about, use filterList. The user may have changed modules since the last message.
			""")
		String chat(String userMessage);
	}
	
	private static Map<String, ChatMemory> chatMemories = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Assistant> assistants = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, EntityTools> entityToolsMap = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, EntityModifyTools> entityModifyToolsMap = Collections.synchronizedMap(new HashMap<>());
	private static final Parser markdownParser = Parser.builder().build();
	private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
	
	@Override
	public String chat(String sessionId, HttpSession httpSession, String message) throws ChatException {
		try {
			Assistant assistant = assistants.get(sessionId);
			
			if (assistant == null) {
				// Get API key from system properties or environment
				String apiKey = System.getProperty("openai.apiKey");
				if (Is.emptyString(apiKey)) {
					apiKey = System.getenv("OPENAI_API_KEY");
				}
				if (Is.emptyString(apiKey)) {
					throw new ChatException("OpenAI API key not configured");
				}
				
				// Create OpenAI chat model
				String modelName = XavaPreferences.getInstance().getChatModelName();
				var modelBuilder = OpenAiChatModel.builder()
					.apiKey(apiKey)
					.modelName(modelName);
				if (XavaPreferences.getInstance().isChatPriorityServiceTier()) {
					modelBuilder.serviceTier("priority");
				}
				var model = modelBuilder.build();
				
				// Create chat memory for this session (keeps last 50 messages)
				// 50 messages to prevent tool calls with large responses from displacing the original question
				ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(50);
				chatMemories.put(sessionId, chatMemory);
				
				// Create assistant with generic tools and memory
				// Get the existing ModuleContext from the session (don't create a new one)
				ModuleContext moduleContext = (ModuleContext) httpSession.getAttribute("context");
				if (moduleContext == null) {
					moduleContext = new ModuleContext();
					httpSession.setAttribute("context", moduleContext);
				}
				String application = MetaModuleFactory.getApplication();
				EntityTools entityTools = new EntityTools(moduleContext, httpSession, application);
				entityToolsMap.put(sessionId, entityTools);
				
				var aiServicesBuilder = AiServices.builder(Assistant.class)
					.chatModel(model)
					.chatMemory(chatMemory)
					.tools(entityTools);
				
				// Add modify tools only if enabled
				if (XavaPreferences.getInstance().isChatModifyDataAvailable()) {
					EntityModifyTools entityModifyTools = new EntityModifyTools(moduleContext, httpSession, application);
					entityModifyToolsMap.put(sessionId, entityModifyTools);
					aiServicesBuilder.tools(entityModifyTools);
				}
				
				assistant = aiServicesBuilder.build();
				
				assistants.put(sessionId, assistant);
			}
			
			// Process the message with the assistant (which has memory)
			String response = assistant.chat(message);
			
			// Convert markdown to HTML
			return markdownToHtml(response);
		} catch (Exception e) {
			throw new ChatException(e.getMessage(), e);
		}
	}
	
	@Override
	public void clearMemory(String sessionId) {
		chatMemories.remove(sessionId);
		assistants.remove(sessionId);
		entityToolsMap.remove(sessionId);
		entityModifyToolsMap.remove(sessionId);
	}
	
	@Override
	public Map<String, String> consumePendingFilterValues(String sessionId) {
		EntityTools entityTools = entityToolsMap.get(sessionId);
		if (entityTools != null) {
			return entityTools.consumePendingFilterValues();
		}
		return null;
	}
	
	@Override
	public boolean consumeRefreshUINeeded(String sessionId) {
		EntityModifyTools entityModifyTools = entityModifyToolsMap.get(sessionId);
		if (entityModifyTools != null) {
			return entityModifyTools.consumeRefreshUINeeded();
		}
		return false;
	}
	
	/**
	 * Converts markdown to HTML using commonmark.
	 */
	private String markdownToHtml(String markdown) {
		Node document = markdownParser.parse(markdown);
		return htmlRenderer.render(document);
	}
	
}

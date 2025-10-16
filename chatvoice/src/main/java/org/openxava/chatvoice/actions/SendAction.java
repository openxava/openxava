package org.openxava.chatvoice.actions;

import org.openxava.actions.*;
import org.openxava.chatvoice.tools.CustomerTools;
import org.openxava.util.*;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class SendAction extends ViewBaseAction {
	
	interface Assistant {
		String chat(String userMessage);
	}

	@Override
	public void execute() throws Exception {
		String message = (String) getView().getValue("message");
		
		if (Is.emptyString(message)) {
			addError("message_required");
			return;
		}
		
		// Obtener API key desde xava.properties
		String apiKey = System.getProperty("openai.apiKey");
		apiKey = "demo"; // tmr
		if (Is.emptyString(apiKey)) {
			apiKey = System.getenv("OPENAI_API_KEY"); // tmr ¿Este nombre? Quizás debería ser configurable, por si queremos uno por aplicación
									// o siguiendo un patron XAVA_OPENAI_API_KEY_NOMBREAPLICACION
		}
		if (Is.emptyString(apiKey)) {
			addError("OpenAI API key not configured. Set 'openai.apiKey' system property or OPENAI_API_KEY environment variable");
			return;
		}
		
		// Crear modelo de chat de OpenAI
		var model = OpenAiChatModel.builder()
			.baseUrl("http://langchain4j.dev/demo/openai/v1")
			.apiKey(apiKey)
			.modelName("gpt-4o-mini") // Modelo más económico y rápido
			.build();
		
		// Crear asistente con tools
		Assistant assistant = AiServices.builder(Assistant.class)
			.chatModel(model)
			.tools(new CustomerTools())
			.build();
		
		// Procesar el mensaje con el asistente
		String response = assistant.chat(message);
		
		// Establecer la respuesta en la vista
		getView().setValue("response", response);
	}

}

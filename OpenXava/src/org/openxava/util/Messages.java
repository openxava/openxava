package org.openxava.util;

import java.text.*;
import java.util.*;
import java.util.regex.*;

import javax.servlet.*;

import org.apache.commons.logging.*;
import org.openxava.model.meta.*;


/**
 * Set of messages. <p>
 * 
 * Uses {@link XavaResources} for doing i18n.
 * 
 * @author Javier Paniza
 * @see XavaResources
 */

public class Messages implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(Messages.class);
	
	public enum Type { ERROR, MESSAGE, INFO, WARNING }   
	
	static class Message implements java.io.Serializable {
		
		private static final Pattern MESSAGE_WITH_BRACKETS = Pattern
				.compile("(\\{[^\\}]+?\\})");
		
		private String id;
		private Object [] argv;
		private Type type; 
		
		public Message(Type type, String id, Object [] argv) { 
			this.type = type;
			this.id = id;
			this.argv = argv;
		}
		
		public Message(String id, Object [] argv) {
			this.type = Type.MESSAGE; 
			this.id = id;
			this.argv = argv;
		}
		
		public Message(String id) {
			this(id, null);
		}
		
		public String getId() {
			return id;
		}
		
		public Type getType() { 
			return type;
		}
		
		public String toString() {
			return toString(Locale.getDefault());
		}
		
		public boolean equals(Object other) {
			if (!(other instanceof Message)) return false;
			Message m = (Message) other;
			return id.equals(m.id);
		}
				
		public int hashCode() {			
			return id.hashCode();
		}
		
		public String toString(Locale locale) {
			if (isAlreadyTranslated(id)) return removeQuotationMarks(id); 
			String m = null;
			try {
				m = getMessage(id, locale);
				return format(m, translate(argv, locale), locale);
			}
			catch (Exception ex) {
				if (argv != null && !(argv[0] instanceof String)
						&& hasMessageBrackets(m)) 
				{
					Matcher matcher = MESSAGE_WITH_BRACKETS.matcher(m);
					StringBuffer sb = new StringBuffer();

					while (matcher.find()) {
						String property = matcher.group().substring(1,
								matcher.group().length() - 1);
						try {
							property = org.apache.commons.beanutils.BeanUtils
									.getProperty(argv[0], property);
						} catch (Exception e) {
							log.warn(XavaResources.getString(
									"property_not_found", property, argv[0]
											.getClass().getSimpleName()));
							property = "{" + property + "}"; 
						}
						matcher.appendReplacement(sb, property);
					}
					matcher.appendTail(sb);					
					return sb.toString();
				}
				if (XavaPreferences.getInstance().isI18nWarnings()) {
					log.warn(XavaResources.getString("label_i18n_warning", id),ex);
				}				
				return id;
			}
		}
		
		private String removeQuotationMarks(String string) { 
			if (string.startsWith("'") && string.endsWith("'") && string.length() > 1) {
				return string.substring(1, string.length() - 1);
			}
			return string;
		}

		private boolean isAlreadyTranslated(String id) { 
			return id.contains(" ") || id.contains("'"); 
		}
		
		private Object[] translate(Object[] argv, Locale locale) {
			if (argv == null || argv.length == 0) return argv; 
			Object [] result = new Object[argv.length];
			for (int i = 0; i < argv.length; i++) {
				Object v = argv[i];
				if (v instanceof String) {					
					if (v.toString().startsWith("'") && v.toString().endsWith("'")) {					
						result[i] = v.toString().substring(1, v.toString().length() - 1);
					}
					else{					
						try {
							result[i] = Labels.removeUnderlined(Labels.get((String)v, locale));
						}
						catch (Exception ex) {
							result[i] = v;
						}	
					}
				}
				else {
					result[i] = v;
				}
			}			
			return result;
		}

		private String getMessage(String id, Locale locale) throws MissingResourceException, XavaException {
			return XavaResources.getString(locale, id);
		}
		
		private String format(String message, Object [] argv, Locale locale) {
			MessageFormat formatter = new MessageFormat("");
			formatter.setLocale(locale);
			formatter.applyPattern(message);
			return formatter.format(argv);		
		}
		private boolean hasMessageBrackets(String message) {
			return MESSAGE_WITH_BRACKETS.matcher(message).find();
		}
	}
	
	private Collection messages; 
	private Collection members;
	private boolean closed = false;	
	
	public boolean contains(String idMessage) {
		if (messages == null) return false; 
		return messages.contains(new Message(idMessage));
	}
	
	public void remove(String idMessage) {
		if (messages == null) return; 
		messages.remove(new Message(idMessage));		
	}
	public void removeAll() {
		if (messages != null) messages.clear(); 
		if (members != null) members.clear();
	}
	
	/**
	 * Clear all error message and does not accept any more messages. <p>
	 * 
	 * If you  call to <code>add</code> after call to this method then 
	 * no exception will throw but the message will not be added.<br>
	 *
	 */
	public void clearAndClose() {
		removeAll();
		closed = true;
	}
	
	public void add(String idMessage, Object ... ids) { 
		add((Type) null, idMessage, ids); 
	}
		
	public void add(Type type, String idMessage, Object ... ids) { 			
		if (closed) return;
		if (ids != null) {
			if (ids.length == 1) {
				addMember(ids[0]);
			}
			else if (ids.length > 1) {
				addMember(ids[0], ids[1]);
			} 
		}
		if (messages == null) messages = new ArrayList(); 
		messages.add(new Message(type, idMessage, ids));
	}
	
	private void addMember(Object member) {
		addMember(member, null);
	}

	private void addMember(Object member, Object model) {
		if (member instanceof String) {
			Object id = null;
			if (model instanceof String) id = model + "." + member; 		
			else id = member;						
			if (members == null) members = new ArrayList();		
			members.add(id);
		}
	}
	
	public boolean contains() {
		return messages != null && !messages.isEmpty(); 
	}
	
	public boolean isEmpty() {
		return messages == null || messages.isEmpty(); 
	}
	
	public String toString() {
		if (messages == null) return ""; 
		Iterator it = messages.iterator();
		StringBuffer r = new StringBuffer();
		while (it.hasNext()) {			
			r.append(it.next());
			r.append('\n');
		}
		return r.toString();
	}
	
	public void add(Messages messages) {	
		if (closed) return;
		if (messages.messages != null) {
			if (this.messages == null) this.messages = new ArrayList();
			this.messages.addAll(messages.messages);
		}		
		if (messages.members != null) {
			if (this.members == null) this.members = new ArrayList();
			this.members.addAll(messages.members);
		}
	}
	
	/**
	 * List of all message texts translated using the default locale.
	 */
	public Collection getStrings() {
		return getStrings(Locale.getDefault());
	}

	/**
	 * List of all message texts translated using the locale of the request.
	 */	
	public Collection getStrings(ServletRequest request) {
		return getStrings(XavaResources.getLocale(request));
	}

	/**
	 * List of all message texts translated using the indicated locale.
	 */	
	public Collection getStrings(Locale locale) {
		if (messages == null) return Collections.EMPTY_LIST; 
		Iterator it = messages.iterator();
		Collection r = new ArrayList();
		while (it.hasNext()) {
			r.add(((Message) it.next()).toString(locale));
		}
		return r;
	}
	
	public Collection getMessagesStrings(ServletRequest request) { 
		return getStrings(Type.MESSAGE, request);
	}
	
	public Collection getWarningsStrings(ServletRequest request) { 
		return getStrings(Type.WARNING, request);
	}	
	
	public Collection getInfosStrings(ServletRequest request) { 
		return getStrings(Type.INFO, request);
	}
	
	private Collection getStrings(Type type, ServletRequest request) { 
		if (messages == null) return Collections.EMPTY_LIST;  
		Iterator it = messages.iterator();
		Collection r = new ArrayList();
		while (it.hasNext()) {
			Message message = (Message) it.next();
			if (message.getType() == type) {
				r.add(message.toString(XavaResources.getLocale(request)));
			}
		}
		return r;
	}

		
	/**
	 * List of all ids of the messages
	 * @return
	 */
	public Collection getIds() {
		if (messages == null) return Collections.EMPTY_LIST; 
		Iterator it = messages.iterator();
		Collection r = new ArrayList();
		while (it.hasNext()) {
			r.add(((Message) it.next()).getId());
		}
		return r;				
	}
	
	public boolean memberHas(MetaMember m) {
		if (members == null) return false;		
		if (m.getMetaModel() != null && members.contains(m.getMetaModel().getName() + "." + m.getName())) return true;
		return members.contains(m.getName());
	}
	
	/**
	 * Qualified names of the members affected for this errors. <p>  
	 */
	public Collection getMembers() {
		return members==null?Collections.EMPTY_LIST:members;
	}
	
}

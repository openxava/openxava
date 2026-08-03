package org.openxava.reports;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * Converts JRXML report templates written for JasperReports 6 or older to the
 * format required by JasperReports 7. <p>
 *
 * JasperReports 7 replaced the Apache Commons Digester based parser with Jackson
 * XML, so the JRXML files created with JasperReports 6 or older can no longer be
 * loaded. OpenXava converts them on the fly, thus the reports of the applications
 * developed with OpenXava 7 or older keep working without changes.<p>
 *
 * The conversion can also be applied to the files themselves, executing:
 * <pre>
 * java -cp openxava.jar org.openxava.reports.JRXMLMigrator src/main/resources/reports
 * </pre>
 *
 * The reports using charts, crosstabs or component elements (as tables, lists or
 * barcodes) are not converted, they have to be converted opening and saving them
 * with <a href="https://community.jaspersoft.com/">Jaspersoft Studio</a> 7.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class JRXMLMigrator {

	private static Log log = LogFactory.getLog(JRXMLMigrator.class);

	private final static String STYLESHEET = "org/openxava/reports/jrxml6to7.xsl";
	private final static String LEGACY_NAMESPACE = "jasperreports.sourceforge.net/jasperreports";

	/**
	 * If the JRXML is in the format of JasperReports 6 or older converts it to the
	 * format of JasperReports 7, otherwise returns it unchanged. <p>
	 *
	 * @param jrxml  The content of the JRXML file. Not null.
	 * @param name  The name of the report, only used for logging and error messages.
	 * @since 8.0
	 */
	public static InputStream migrateIfNeeded(InputStream jrxml, String name) throws IOException, XavaException {
		byte [] content = jrxml.readAllBytes();
		jrxml.close();
		if (!isLegacy(content)) return new ByteArrayInputStream(content);
		log.info(XavaResources.getString("jrxml_migrating", name));
		return new ByteArrayInputStream(migrate(content, name));
	}

	/**
	 * If the JRXML is in the format of JasperReports 6 or older. <p>
	 *
	 * @since 8.0
	 */
	public static boolean isLegacy(byte [] jrxml) {
		String header = new String(jrxml, 0, Math.min(jrxml.length, 2000), StandardCharsets.ISO_8859_1);
		return header.contains(LEGACY_NAMESPACE) ||
			new String(jrxml, StandardCharsets.ISO_8859_1).contains("<reportElement");
	}

	/**
	 * Converts a JRXML from the format of JasperReports 6 or older to the format
	 * of JasperReports 7. <p>
	 *
	 * @param jrxml  The content of the JRXML file. Not null.
	 * @param name  The name of the report, only used in error messages.
	 * @since 8.0
	 */
	public static byte [] migrate(byte [] jrxml, String name) throws XavaException {
		try {
			InputStream stylesheet = JRXMLMigrator.class.getClassLoader().getResourceAsStream(STYLESHEET);
			if (stylesheet == null) throw new XavaException("resource_not_found", STYLESHEET);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
			MessagesCollector messages = new MessagesCollector(); // The xsl:message of the unsupported constructs
			transformer.setErrorListener(messages);
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			transformer.transform(new StreamSource(new ByteArrayInputStream(jrxml)), new StreamResult(result));
			return result.toByteArray();
		}
		catch (TransformerException ex) {
			throw new XavaException("jrxml_migration_failed", name, unsupported(ex));
		}
	}

	/**
	 * Converts in place the JRXML files in the format of JasperReports 6 or older
	 * found in the indicated files or folders. <p>
	 *
	 * @since 8.0
	 */
	public static void main(String [] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage: java org.openxava.reports.JRXMLMigrator <file or folder> ...");
			System.exit(1);
		}
		int migrated = 0;
		int failed = 0;
		for (String arg: args) {
			for (Path file: jrxmls(Path.of(arg))) {
				byte [] content = Files.readAllBytes(file);
				if (!isLegacy(content)) {
					System.out.println(file + ": already in JasperReports 7 format");
					continue;
				}
				try {
					Files.write(file, migrate(content, file.toString()));
					System.out.println(file + ": converted");
					migrated++;
				}
				catch (XavaException ex) {
					System.out.println(file + ": NOT CONVERTED, " + ex.getMessage());
					failed++;
				}
			}
		}
		System.out.println(migrated + " report(s) converted, " + failed + " report(s) not converted");
		if (failed > 0) System.exit(2);
	}

	private static List<Path> jrxmls(Path path) throws IOException {
		if (!Files.isDirectory(path)) return List.of(path);
		try (Stream<Path> files = Files.walk(path)) {
			return files.filter(file -> file.toString().endsWith(".jrxml")).sorted().toList();
		}
	}

	private static String unsupported(TransformerException ex) {
		String message = MessagesCollector.lastUnsupported();
		if (message != null) return message;
		return ex.getMessage() == null ? "" : ex.getMessage();
	}

	/**
	 * The content of a terminating xsl:message is sent to the error listener, thus
	 * it is collected here to know what makes the conversion fail.
	 */
	private static class MessagesCollector implements ErrorListener {

		private final static String PREFIX = "jrxml_migration_unsupported_";
		private final static ThreadLocal<String> last = new ThreadLocal<String>();

		static String lastUnsupported() {
			return last.get();
		}

		MessagesCollector() {
			last.remove();
		}

		public void warning(TransformerException ex) {
			collect(ex);
		}

		public void error(TransformerException ex) throws TransformerException {
			collect(ex);
			throw ex;
		}

		public void fatalError(TransformerException ex) throws TransformerException {
			collect(ex);
			throw ex;
		}

		private void collect(TransformerException ex) {
			String message = ex.getMessage();
			if (message == null) return;
			int i = message.indexOf(PREFIX);
			if (i >= 0) last.set(message.substring(i + PREFIX.length()).trim().replace(':', ' '));
		}

	}

}

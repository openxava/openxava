// Renames the Spring Boot launcher class from 'Application' to '<ArtifactId>Application',
// following the Spring Initializr naming convention (e.g. artifactId 'ventas' -> 'VentasApplication').
// This script is executed automatically by Maven when a project is generated from this archetype.

String toClassName(String artifactId) {
    StringBuilder sb = new StringBuilder()
    artifactId.split("[^a-zA-Z0-9]+").each { part ->
        if (part) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1))
    }
    return sb.toString()
}

def artifactId = request.artifactId
def projectDir = new File(request.outputDirectory, artifactId)
if (!projectDir.exists()) projectDir = new File(request.outputDirectory)

def newClass = toClassName(artifactId) + "Application"

// Locate and rename the launcher class
def javaRoot = new File(projectDir, "src/main/java")
def appFile = null
if (javaRoot.exists()) {
    javaRoot.eachFileRecurse { f ->
        if (f.isFile() && f.name == "Application.java") appFile = f
    }
}
if (appFile != null) {
    def text = appFile.getText("ISO-8859-1")
    text = text.replace("__artifactId__", artifactId)
    text = text.replace("class Application extends", "class ${newClass} extends")
    text = text.replace("SpringApplication.run(Application.class", "SpringApplication.run(${newClass}.class")
    def newFile = new File(appFile.parentFile, "${newClass}.java")
    newFile.setText(text, "ISO-8859-1")
    appFile.delete()
    println "Renamed launcher class to ${newClass}"
}

// Update the mainClass reference in pom.xml
def pom = new File(projectDir, "pom.xml")
if (pom.exists()) {
    def ptext = pom.getText("ISO-8859-1")
    ptext = ptext.replace('.Application</mainClass>', ".${newClass}</mainClass>")
    pom.setText(ptext, "ISO-8859-1")
}

// Pin ISO-8859-1 for all source directories in .idea/encodings.xml.
// IntelliJ's Maven importer only applies project.build.sourceEncoding to
// src/main/java and src/main/resources, leaving test sources at the default
// UTF-8, which garbles ISO-8859-1 accents in test classes.
def ideaDir = new File(projectDir, ".idea")
ideaDir.mkdirs()
def encodings = new File(ideaDir, "encodings.xml")
encodings.setText('''<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="Encoding">
    <file url="file://$PROJECT_DIR$/src/main/java" charset="ISO-8859-1" />
    <file url="file://$PROJECT_DIR$/src/main/resources" charset="ISO-8859-1" />
    <file url="file://$PROJECT_DIR$/src/main/webapp" charset="ISO-8859-1" />
    <file url="file://$PROJECT_DIR$/src/test/java" charset="ISO-8859-1" />
    <file url="file://$PROJECT_DIR$/src/test/resources" charset="ISO-8859-1" />
    <file url="file://$PROJECT_DIR$/data" charset="ISO-8859-1" />
    <file url="file://$PROJECT_DIR$/test-files" charset="ISO-8859-1" />
  </component>
</project>
''', "ISO-8859-1")
println "Created .idea/encodings.xml with ISO-8859-1 for all source directories"

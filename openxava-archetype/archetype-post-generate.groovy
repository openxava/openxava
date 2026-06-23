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
    def text = appFile.getText("UTF-8")
    text = text.replace("class Application extends", "class ${newClass} extends")
    text = text.replace("SpringApplication.run(Application.class", "SpringApplication.run(${newClass}.class")
    def newFile = new File(appFile.parentFile, "${newClass}.java")
    newFile.setText(text, "UTF-8")
    appFile.delete()
    println "Renamed launcher class to ${newClass}"
}

// Update the mainClass reference in pom.xml
def pom = new File(projectDir, "pom.xml")
if (pom.exists()) {
    def ptext = pom.getText("UTF-8")
    ptext = ptext.replace('.Application</mainClass>', ".${newClass}</mainClass>")
    pom.setText(ptext, "UTF-8")
}

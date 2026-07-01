@echo off
REM Deploy to Maven Central
REM Step 1 (manual): Change version number in archetype.properties, archetype-pom.xml and pom.xml

REM Step 2: Generate archetype from project
call mvn clean archetype:create-from-project -Darchetype.properties="archetype.properties"
if %errorlevel% neq 0 (
    echo ERROR: archetype:create-from-project failed
    exit /b %errorlevel%
)

REM Step 3: Copy archetype-metadata.xml
copy archetype-metadata.xml target\generated-sources\archetype\src\main\resources\META-INF\maven
if %errorlevel% neq 0 (
    echo ERROR: copy archetype-metadata.xml failed
    exit /b %errorlevel%
)

REM Step 4: Copy archetype-pom.xml as pom.xml
copy archetype-pom.xml target\generated-sources\archetype\pom.xml
if %errorlevel% neq 0 (
    echo ERROR: copy archetype-pom.xml failed
    exit /b %errorlevel%
)

REM Step 5: Copy archetype-post-generate.groovy (renames launcher class to <ArtifactId>Application)
copy archetype-post-generate.groovy target\generated-sources\archetype\src\main\resources\META-INF
if %errorlevel% neq 0 (
    echo ERROR: copy archetype-post-generate.groovy failed
    exit /b %errorlevel%
)

REM Step 6: Deploy from generated archetype directory
cd target\generated-sources\archetype
call mvn clean deploy

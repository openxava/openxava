# Openxava Application

## Requirements

- java >= 8
- ant
- maven

## Global Steps

- configure the database connection: `../persistence/META-INF/persistence.xml`

## Developer

Just open this project in your eclipse, add a tomcat and run this class: `../src/_run/AppLauncher.java`

## Production

- Build the image
```
docker build -t openxava .
```

- Run as container

```
docker run --name openxava -d --rm -p 8080:8080 \
-e JAVA_RAM="-Xms125m -Xmx512m" \
openxava
```

## Roadmap

- add environment var to deploy the app under context or at root
- add environment variables to handle database connection instead harcoded values
- add environment variables to manage user like `admin=${ADMIN_PASSWORD}`
- add environment variables to enable/disable main settings like `enableFirstSteps, admin password suggest`  in files: xava.properties, naviox.properties, etc
- add captcha to prevent FBA at login

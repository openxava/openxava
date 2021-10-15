#!/bin/bash

ENV_FILE=env
ENV_HOME=/var
TOMCAT_LOCATION=/usr/local/tomcat/
mkdir -p /log
source_code_path="/app"
log_file="/log/app.log"
war_name=${WAR_NAME}


function echo_log {
  DATE='date +%Y/%m/%d:%H:%M:%S'
  echo `$DATE`" $1"
  echo `$DATE`" $1" >> $log_file
}

function export_java_env {
  java_environments=""
  echo_log "## Exporting Java Environment Variables ##"

  while IFS='=' read -r name value ; do
    if [[ $name == *'JAVA_'* ]] && [[ $name != "JAVA_HOME" ]] && [[ $name != "JAVA_VERSION" ]]; then
      java_environments="$java_environments  ${!name} ";
    fi
  done < <(env)
  export CATALINA_OPTS="$java_environments"

}

function download_env_variables {

  if [[ "x${CONFIGURATOR_GET_VARIABLES_FULL_URL}" = "x"  || "x${CONFIGURATOR_AUTH_HEADER}" = "x" ]]; then
    echo_log ""
    echo_log "Configurator variables are not provided. Variables will not be downloaded"
    return 0
  fi

 echo_log ""
 echo_log "starting to download environment variables"
 http_response=$(curl -s -o curl_response_file -w "%{http_code}" -H "$CONFIGURATOR_AUTH_HEADER" ${CONFIGURATOR_GET_VARIABLES_FULL_URL})

 echo_log "download status:  $http_response"
 if [ $http_response == "200" ]; then
    mv curl_response_file $ENV_HOME/$ENV_FILE
    echo_log "exporting Environment Variables"
    source $ENV_HOME/$ENV_FILE
 else

    echo_log "download response: $(cat curl_response_file)"
    echo_log "new environment variables could not be obtained from remote service"

    if [ -e $ENV_HOME/$ENV_FILE ]; then
       echo_log "exporting old env variables"
       source $ENV_HOME/$ENV_FILE
    else
       echo_log "environment file not found: $ENV_HOME/$ENV_FILE"
       echo_log "if variables will not download, don't pass CONFIGURATOR_ variables and try again."
       exit 1
    fi
 fi

}

function parse_env_variables {

  echo_log ""
  echo_log "##### Parsing java JDK variables #####"

  export_java_env

}

function start_app {
  echo_log ""
  echo_log "##### Starting app ##### "
  echo_log "## target path: $source_code_path ##"
  ls -la $source_code_path/target
  echo_log "## tomcat webapps path: $TOMCAT_LOCATION/webapps ##"
  ls -la $TOMCAT_LOCATION/webapps/

  if [ -n "$war_name" ]; then
    war_file_name=$war_name
    echo_log "## war argument detected : $war_name ##"
  else

    if [ "$(ls -A $source_code_path)" ]; then
      cd  $source_code_path/target
      war_file_number=$(find -name "*.war" | wc -l)
      war_file_name=
      if [ "$war_file_number" -gt "1" ] ; then
        echo_log "## Several war files were detected. We need just one!! ##"
        war_files=$(find -name "*.war")
        echo_log "$war_files"
        echo_log "## You should enter a unique war name. Example -e \"WAR_NAME=lassie.war\" ##"
        exit 1
      else
        # get unique war name
        war_file_name=$(find -name "*.war")
      fi
    fi
  fi
  cd /app/target
  echo_log "## war to deploy: $war_file_name ##"
  war_name="${war_file_name%.*}"

  if [ -d "$TOMCAT_LOCATION/webapps/$war_name" ]
  then
    echo_log "## App is already deployed. ##"
  else
    echo_log "## Moving war to webapps: ##"
    ls -la
    echo_log "## cp $war_file_name $TOMCAT_LOCATION/webapps/ ##"
    cp $war_file_name $TOMCAT_LOCATION/webapps/
    echo_log "webapps content:"
    ls -la
  fi

  echo_log ""
  echo_log "##### Starting tomcat #####"
  echo_log ""
  java -version
  echo "CATALINA_OPTS: $CATALINA_OPTS"
  bash $TOMCAT_LOCATION/bin/catalina.sh run

}

########################
# Scripts starts here
########################
download_env_variables
parse_env_variables
start_app

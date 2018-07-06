def CONTAINER_NAME = "cicd"
def DOCKER_HUB_USER = "Crussassin"
def APP_HTTP_PORT = "5000"
def HOST = "local"

node {
	stage('Initialize') {
		def dockerHome = tool 'myDocker'
		env.PATH = "${dockerHome}/bin:${env.PATH}"
	}

	stage('Stop working Image') {
		sleep 5
		sh "docker stop $CONTAINER_NAME"
	}
	
	stage('Take and run Image from DockerHub') {
		IMAGE_NAME = DOCKER_HUB_USER + "/" + CONTAINER_NAME 
		sh "docker pull $IMAGE_NAME:${env.CONTAINER_TAG}"
		sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME docker.io/$IMAGE_NAME:${env.CONTAINER_TAG}"
	}

	stage('Integration tests') {
		sleep 5
		status = sh(returnStdout: true, script: "docker inspect $CONTAINER_NAME --format='{{.State.Status}}'").trim()
		if (status != 'running') {
			currentBuild.result = 'FAILED'
			sh "exit ${status}"
		}
	}

}

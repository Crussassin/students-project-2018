def CONTAINER_NAME = "ci_cd"
def DOCKER_HUB_USER = "crussassin"
def APP_HTTP_PORT = "8080"
def HOST = "local"

node {

	stage('Initialize') {
		def dockerHome = tool 'myDocker'
		env.PATH = "${dockerHome}/bin:${env.PATH}"
	}
	stage('Checkout') {
	deleteDir()
	checkout scm
	}
	
	
	
	stage('Deploy to staging') {
		try {
		
			echo "Deploy tag: ${env.IMAGE_TAG}"
			ansiblePlaybook colorized: true,
			limit: "${HOST}",
			credentialsId: 'ssh-key-jenkins',
			installation: 'ansible',
			inventory: 'ansible/hosts',
			playbook: 'ansible/play.yml',
			vaultCredentialsId: 'ansible_vault_credentials'
			currentBuild.result = 'SUCCESS'
			} catch (Exception err) {
		currentBuild.result = 'FAILED'
			}
		}
			
	

	stage('Take and run Image from DockerHub') {
		try {
			IMAGE_NAME = DOCKER_HUB_USER + "/" + CONTAINER_NAME 
			sh "docker pull $IMAGE_NAME:${env.CONTAINER_TAG}"
			sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME docker.io/$IMAGE_NAME:${env.CONTAINER_TAG}"
			currentBuild.result = 'SUCCESS'
		} catch (Exception err) {
		currentBuild.result = 'FAILED'
			}
		}	
	
	

	stage('Integration tests') {
				
		try {
		
		status = sh(returnStdout: true, script: "docker inspect $CONTAINER_NAME --format='{{.State.Status}}'").trim()
		if (status != 'running') {
			currentBuild.result = 'FAILED'
			sh "exit 1"
		}
		sleep 5
		currentBuild.result = 'SUCCESS'
		} catch (Exception err) {
		currentBuild.result = 'FAILED'
		}

	}
}

def CONTAINER_NAME = "ci_job"
def DOCKER_HUB_USER = "Crusaasin"
def APP_HTTP_PORT = "5000"
def IMAGE_NAME = ''
def CONTAINER_TAG = ''

node {
	stage('Initialize') {
		def dockerHome = tool 'myDocker'
		env.PATH = "${dockerHome}/bin:${env.PATH}"
	}

	stage('Checkout') {
		deleteDir()
		checkout scm
	}
	
	stage('Buildingg') {
 		
		CONTAINER_TAG = sh(returnStdout: true, script: "git describe --tags 2>/dev/null").trim()		
		if (CONTAINER_TAG == '') {
			currentBuild.result = 'FAILED'
			sh "exit 1"
		}
		IMAGE_NAME = DOCKER_HUB_USER + "/" + CONTAINER_NAME + ":" + CONTAINER_TAG
		sh "docker build -t $IMAGE_NAME --pull --no-cache ."
		echo "Image $IMAGE_NAME build complete"
	}

	stage('Unit tests') {
		try {
		
			sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME $IMAGE_NAME"
			sleep 5
			status = sh(returnStdout: true, script: "docker inspect $CONTAINER_NAME --format='{{.State.Status}}'").trim()
			if (status != '0') {
				currentBuild.result = 'FAILED'
				sh "exit ${status}"
			}
		currentBuild.result = 'SUCCESS' 
			} catch (Exception err) {
                currentBuild.result = 'FAILED'
        }		 
	}

	stage('Push to dockerhub') {
		withCredentials([usernamePassword(credentialsId: 'dockerHub-crussassin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
			sh "docker login -u $DOCKER_HUB_USER -p $PASSWORD"
			sh "docker tag $DOCKER_HUB_USER/$CONTAINER_NAME $DOCKER_HUB_USER/$DOCKER_HUB_USER/$CONTAINER_NAME"
			sh "docker push $IMAGE_NAME"
			echo "Image $IMAGE_NAME push complete"
		}
	}
}


pipeline {
    agent any
    
    environment {
        DOCKERHUB_REPO = 'jtan22/microservice-frontend'
        DOCKERHUB_CREDENTIAL = 'dockerhub'
    }

    stages {
        stage('Build Package') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Build Image') {
            steps {
                script {
                    app = docker.build("${DOCKERHUB_REPO}:${env.BUILD_NUMBER}")
                }
            }
        }
        stage('Push Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKERHUB_CREDENTIAL) {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage('Deploy Image') {
            steps {
                sh 'kubectl apply -f deployment.yaml'
            }
        }
    }
}
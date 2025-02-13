pipeline {
    agent any
    
    environment {
        DOCKERHUB_REPO = 'jtan22/microservice-frontend'
        DOCKERHUB_CREDENTIAL = 'dockerhub'
    }

    script {
        def app
    }
    
    stages {
        stage('Set Docker Context') {
            steps {
                sh 'docker context use default'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    app = docker.build("${DOCKERHUB_REPO}:${env.BUILD_NUMBER}")
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKERHUB_CREDENTIAL) {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
    }
}
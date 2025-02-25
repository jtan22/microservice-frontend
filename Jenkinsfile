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
        stage('Get Project Version') {
            steps {
                script {
                    env.PROJECT_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    env.DOCKER_VERSION = env.PROJECT_VERSION
                    if (env.BRANCH_NAME == 'main') {
                        env.DOCKER_VERSION = env.PROJECT_VERSION.replace('-SNAPSHOT', '')
                    }
                }
            }
        }
        stage('Build Image') {
            steps {
                script {
                    app = docker.build("${DOCKERHUB_REPO}:${env.DOCKER_VERSION}", "--build-arg JAR_FILE_VERSION=${env.PROJECT_VERSION} .")
                }
            }
        }
        stage('Push Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKERHUB_CREDENTIAL) {
                        app.push("${env.DOCKER_VERSION}")
                        if (env.BRANCH_NAME == 'main') {
                            app.push("latest")
                        }
                    }
                }
            }
        }
        stage('Deploy Image') {
            steps {
                sh "sed 's/\${PROJECT_VERSION}/${env.DOCKER_VERSION}/g' deployment.yaml | kubectl apply -f -"
            }
        }
        stage('Increment Version') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def newVersion = env.DOCKER_VERSION.replaceAll(/(\d+)\.(\d+)\.(\d+)/) { match ->
                        def (major, minor, patch) = [match[1], match[2], match[3]]
                        println "patch: ${patch}"
                        return "${major}.${minor}.${(patch.toInteger() + 1)}-SNAPSHOT"
                    }
                    sh 'git checkout main'
                    sh "mvn versions:set -DnewVersion=${newVersion}"
                    sh "mvn versions:commit"
                    sh 'git add pom.xml'
                    sh "git commit -m 'Increment version to ${newVersion}'"
                    sh 'git push origin main'
                }
            }
        }
    }
}
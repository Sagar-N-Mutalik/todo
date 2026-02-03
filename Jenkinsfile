pipeline {
    agent any

    tools {
        // Must match the name in "Manage Jenkins -> Tools"
        jdk 'jdk-21' 
    }

    environment {
        // App Settings
        APP_NAME = 'todo-app'
        IMAGE_TAG = "${BUILD_NUMBER}" // Or use 'latest' or Git Commit Hash
        DOCKER_USER = 'your-dockerhub-username' 
        
        // SonarQube Settings (Matches Global Config Name)
        SONAR_SERVER = 'SonarQube Server' 
    }

    stages {
        stage('Initialize') {
            steps {
                echo '=== Initializing Pipeline ==='
                // Ensure the wrapper is executable
                sh 'chmod +x mvnw'
                sh './mvnw -version'
            }
        }

        stage('Checkout') {
            steps {
                // Jenkins automatically checks out code from Git, 
                // but explicit checkout ensures clean slate
                checkout scm
            }
        }

        stage('Build & Unit Tests') {
            steps {
                echo '=== Building & Running Unit Tests ==='
                // Runs compilation and unit tests (JUnit)
                sh './mvnw clean test'
            }
            post {
                always {
                    // Archive JUnit results so Jenkins can graph them
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Quality (SonarQube)') {
            steps {
                echo '=== Running Static Analysis ==='
                // 'withSonarQubeEnv' injects the server details automatically
                withSonarQubeEnv(env.SONAR_SERVER) {
                    // We pass the credentials directly to the maven command
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh './mvnw sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '=== Checking Quality Gate ==='
                // This pauses the pipeline until SonarQube reports back (Pass/Fail)
                // Timeout prevents it from hanging forever if Sonar is down
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package Artifact') {
            steps {
                echo '=== Packaging JAR ==='
                // Skip tests here because we already ran them
                sh './mvnw package -DskipTests'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo '=== Archiving JAR ==='
                // Saves the JAR in Jenkins for download later
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                echo '=== Building Docker Image ==='
                script {
                    // Build image: username/appname:buildnumber
                    dockerImage = docker.build("${env.DOCKER_USER}/${env.APP_NAME}:${env.IMAGE_TAG}")
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo '=== Pushing to Registry ==='
                script {
                    // Log in to Docker Hub and Push
                    docker.withRegistry('', 'docker-hub-credentials') {
                        dockerImage.push()
                        dockerImage.push('latest') // Also push 'latest' tag
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '=== Deploying to Production ==='
                // Example: SSH into a server and update the container
                // Requires 'SSH Agent' plugin
                sshagent(['deploy-server-ssh']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no user@your-server-ip '
                            docker pull ${env.DOCKER_USER}/${env.APP_NAME}:latest
                            docker stop ${env.APP_NAME} || true
                            docker rm ${env.APP_NAME} || true
                            docker run -d --name ${env.APP_NAME} -p 8080:8080 ${env.DOCKER_USER}/${env.APP_NAME}:latest
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline Succeeded!'
            // Optional: Slack/Email Notification
            // mail to: 'you@example.com', subject: "Success: ${env.JOB_NAME}", body: "Build #${env.BUILD_NUMBER} Passed."
        }
        failure {
            echo ' Pipeline Failed.'
            // mail to: 'you@example.com', subject: "Failed: ${env.JOB_NAME}", body: "Build #${env.BUILD_NUMBER} Failed. Check logs."
        }
        always {
            // Cleanup Docker images to save disk space on Jenkins agent
            sh "docker rmi ${env.DOCKER_USER}/${env.APP_NAME}:${env.IMAGE_TAG} || true"
            sh "docker rmi ${env.DOCKER_USER}/${env.APP_NAME}:latest || true"
            cleanWs() // Clean Workspace
        }
    }
}
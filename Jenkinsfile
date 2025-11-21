// Jenkinsfile (Declarative)
pipeline {
  agent any

environment {
    // Change this to match your Spring Boot JAR name in /target directory
    APP_NAME = 'demo-0.0.1-SNAPSHOT.jar' 
    REMOTE_USER = 'jenkins-deploy' // User on App Server
    REMOTE_HOST = '172.31.20.108' 
    DEPLOY_DIR = '/home/jenkins-deploy/app'
}

stages {
    stage('Checkout Source') {
        steps {
            echo 'Cloning repository...'
            // The 'git' step is automatically configured to use the job's SCM settings
            checkout scm
        }
    }

    stage('Build with Maven') {
        steps {
            echo 'Building Spring Boot application...'
            // The 'sh' step executes commands on the Jenkins server
            sh "mvn clean package -DskipTests"
        }
    }

    stage('Prepare App Server') {
        steps {
            echo "Preparing deployment directory on App Server: ${DEPLOY_DIR}"
            // Use 'sh' with SSH to execute commands on the remote server
            sh """
                ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "
                mkdir -p ${DEPLOY_DIR}
                # Stop the currently running application instance 
                APP_PID=\$(sudo lsof -t -i:8080)
                if [ ! -z "\$APP_PID" ]; then
                    sudo kill -9 \$APP_PID
                    echo 'Stopped existing application (PID: '\$APP_PID')'
                else
                    echo 'No existing application found to stop.'
                fi
                "
            """
        }
    }
    
    stage('Deploy Artifact to App Server') {
        steps {
            echo "Deploying JAR to App Server..."
            // Transfer the JAR file using SCP via 'Publish Over SSH'
            // This requires the 'Publish Over SSH' plugin to be configured in Jenkins
            sshPublisher(publishers: [
                sshPublisherDesc(
                    configName: 'jenkins-deploy', // MUST match the name from Configure System
                    transfers: [
                        sshTransfer(
                            sourceFiles: "target/${APP_NAME}", // Source file on Jenkins server
                            removePrefix: 'target',
                            remoteDirectory: "${DEPLOY_DIR}", // Destination directory on App Server
                            execCommand: """
                                echo "Deployment successful. Starting new application..."
                                # Start the new application in background using nohup
                                nohup java -jar ${DEPLOY_DIR}/${APP_NAME} > ${DEPLOY_DIR}/app.log 2>&1 &
                                echo "Application started on port 8080."
                            """
                        )
                    ]
                )
            ])
        }
    }
}

  post {
    success { echo "Deployed successfully: ${env.BUILD_URL}" }
    failure { echo "Deployment failed, check logs." }
  }
}

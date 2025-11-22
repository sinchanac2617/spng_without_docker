pipeline {
    agent any

    /***** ENVIRONMENT CONFIG *****/
    environment {
        // Deployment / server details
        APP_NAME    = "myapp"                    // systemd service name: myapp.service
        APP_SERVER  = "172.31.17.196"            // PRIVATE IP of app server (change if needed)
        DEPLOY_USER = "deploy"                   // user on app server
        DEPLOY_DIR  = "/opt/myapp"               // directory on app server
        JAR_NAME    = "demo-0.0.1-SNAPSHOT.jar"  // jar file name (your build output)
        SERVER_PORT = "8080"                     // Spring Boot port

        // Jenkins SSH credentials (must match ID in Jenkins)
        SSH_CRED_ID = "app-server-ssh"

        // Git repo
        GIT_REPO   = "https://github.com/Vishnu2663/springboot_cicd_jenkins_project.git"
        GIT_BRANCH = "main"
    }

    options {
        // avoid extra default checkout
        skipDefaultCheckout(true)
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out branch ${GIT_BRANCH} from ${GIT_REPO}"
                git branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Build') {
            steps {
                echo "Building Maven project..."
                sh "mvn -version"
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Copy Artifact to App Server') {
            steps {
                echo "Copying JAR to app server ${APP_SERVER}..."
                sshagent (credentials: [env.SSH_CRED_ID]) {
                    sh """
                        set -ex
                        # Exact jar file that must exist after build
                        JAR_FILE="target/${JAR_NAME}"

                        if [ ! -f "\$JAR_FILE" ]; then
                          echo "ERROR: JAR not found: \$JAR_FILE"
                          ls -R
                          exit 1
                        fi

                        echo "Built JAR: \$JAR_FILE"

                        # Ensure deploy directory exists on app server
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER} "mkdir -p ${DEPLOY_DIR}"

                        # Copy jar to app server with same name
                        scp -o StrictHostKeyChecking=no "\$JAR_FILE" ${DEPLOY_USER}@${APP_SERVER}:${DEPLOY_DIR}/${JAR_NAME}
                    """
                }
            }
        }

        stage('Restart Service on App Server') {
            steps {
                echo "Restarting systemd service ${APP_NAME}.service on ${APP_SERVER}..."
                sshagent (credentials: [env.SSH_CRED_ID]) {
                    sh """
                        set -ex
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER} \\
                          'sudo systemctl restart ${APP_NAME}.service && sudo systemctl status ${APP_NAME}.service --no-pager'
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "Running health check on http://${APP_SERVER}:${SERVER_PORT}/ ..."
                sh """
                    set +e
                    for i in {1..12}; do
                      if curl -sSf http://${APP_SERVER}:${SERVER_PORT}/ > /dev/null; then
                        echo "Application is UP on attempt \$i"
                        exit 0
                      fi
                      echo "Health check attempt \$i failed, retrying in 5 seconds..."
                      sleep 5
                    done
                    echo "Application did not become healthy in time."
                    exit 1
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful! App should be reachable at: http://${APP_SERVER}:${SERVER_PORT}/"
        }
        failure {
            echo "❌ Pipeline failed. Check the failed stage logs for details."
        }
    }
}

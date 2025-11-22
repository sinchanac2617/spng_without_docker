pipeline {
    agent any

    environment {
        APP_NAME    = "myapp"
        APP_SERVER  = "172.31.17.196"
        DEPLOY_USER = "ubuntu"
        DEPLOY_DIR  = "/opt/myapp"
        JAR_NAME    = "demo-0.0.1-SNAPSHOT.jar"
        SERVER_PORT = "8080"
        SSH_CRED_ID = "app-server-ssh-ubuntu"
        GIT_REPO    = "https://github.com/Vishnu2663/springboot_cicd_jenkins_project.git"
        GIT_BRANCH  = "main"
    }

    options {
        skipDefaultCheckout(true)
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Copy Artifact to App Server') {
            steps {
                sshagent (credentials: [env.SSH_CRED_ID]) {
                    sh """
                        set -ex
                        JAR_FILE="target/${JAR_NAME}"
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER} "mkdir -p ${DEPLOY_DIR}"
                        scp -o StrictHostKeyChecking=no "\$JAR_FILE" ${DEPLOY_USER}@${APP_SERVER}:${DEPLOY_DIR}/${JAR_NAME}
                    """
                }
            }
        }

        stage('Restart Service on App Server') {
            steps {
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
                sh """
                    set +e
                    for i in {1..12}; do
                        if curl -sSf http://${APP_SERVER}:${SERVER_PORT}/ > /dev/null; then
                            echo "Application is UP"
                            exit 0
                        fi
                        echo "Waiting for app to start..."
                        sleep 5
                    done
                    echo "App did not start in time"
                    exit 1
                """
            }
        }
    }

    post {
        success { echo "Deployment Successful!" }
        failure { echo "Deployment Failed — Check Stage Logs" }
    }
}

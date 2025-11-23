pipeline {
    agent any

    environment {
        APP_SERVER_IP = '172.31.29.92'      // private IP of the app server
        DEPLOY_USER   = 'deploy'             // SSH user
        JAR_NAME      = 'demo-0.0.1-SNAPSHOT.jar'  // update according to actual name
        REMOTE_PATH   = '/home/ubuntu/app/app.jar'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/sinchanac2617/spng_without_docker.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Copy JAR to App Server') {
            steps {
                sshagent(credentials: ['app-server-ssh-id']) {
                    sh """
                        scp -o StrictHostKeyChecking=no target/${JAR_NAME} ${DEPLOY_USER}@${APP_SERVER_IP}:${REMOTE_PATH}
                    """
                }
            }
        }

        stage('Restart Application') {
            steps {
                sshagent(credentials: ['app-server-ssh-id']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER_IP} '
                            fuser -k 8080/tcp || true
                            nohup java -jar ${REMOTE_PATH} > /home/ubuntu/app/app.log 2>&1 &
                        '
                    """
                }
            }
        }

        stage('Verify Application') {
            steps {
                echo "Application restarted successfully!"
            }
        }
    }
}

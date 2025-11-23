pipeline {
    agent any

    environment {
        APP_SERVER_IP = '172.31.29.92'
        DEPLOY_USER   = 'deploy'
        JAR_NAME      = 'demo-0.0.1-SNAPSHOT.jar'
        REMOTE_PATH   = '/opt/myapp/app.jar'
    }

    stages {

        stage('Checkout') {
            steps {
                git url: 'https://github.com/sinchanac2617/spng_without_docker.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Copy JAR to App Server') {
            steps {
                sshagent (credentials: ['jenkins-ssh']) {
                    sh '''
                    scp -o StrictHostKeyChecking=no target/$JAR_NAME ${DEPLOY_USER}@${APP_SERVER_IP}:${REMOTE_PATH}
                    '''
                }
            }
        }

        stage('Restart Application') {
            steps {
                sshagent (credentials: ['jenkins-ssh']) {
                    sh '''
                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER_IP} "sudo systemctl restart myapp.service"
                    '''
                }
            }
        }

        stage('Verify Application') {
            steps {
                sshagent (credentials: ['jenkins-ssh']) {
                    sh '''
                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER_IP} "sudo systemctl status myapp.service --no-pager"
                    '''
                }
            }
        }
    }
}

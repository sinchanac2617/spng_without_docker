pipeline {
    agent any

    environment {
        APP_SERVER_IP = '172.31.16.31'
        DEPLOY_USER   = 'deploy'
        JAR_NAME      = 'demo-0.0.1-SNAPSHOT.jar '
        REMOTE_PATH   = '/opt/myapp/app.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Vishnu2663/springboot_cicd_jenkins_project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Copy JAR to App Server') {
            steps {
                sh '''
                scp -o StrictHostKeyChecking=no target/$JAR_NAME ${DEPLOY_USER}@${APP_SERVER_IP}:${REMOTE_PATH}
                '''
            }
        }

        stage('Restart Service on App Server') {
            steps {
                sh '''
                ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER_IP} "sudo -n /usr/bin/systemctl restart myapp.service"
                '''
            }
        }

        stage('Check Service Status') {
            steps {
                sh '''
                ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${APP_SERVER_IP} "sudo -n /usr/bin/systemctl status myapp.service --no-pager"
                '''
            }
        }
    }
}

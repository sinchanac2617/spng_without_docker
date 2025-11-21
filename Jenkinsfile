pipeline {

    agent none

    tools {
        maven 'maven'       // ✔ Uses Jenkins Maven tool
    }

    stages {

        stage('Checkout Code') {
            agent { label 'worker-node' }
            steps {
                echo "===== CHECKING OUT FROM GITHUB ====="
                git branch: 'main', url: 'https://github.com/sinchanac2617/springboot_without_docker.git'
            }
        }

        stage('Build on Worker Node') {
            agent { label 'worker-node' }
            steps {
                sh '''
                    echo "===== JAVA VERSION ====="
                    java -version

                    echo "===== MAVEN VERSION (JENKINS TOOL) ====="
                    mvn -version

                    echo "===== BUILDING PROJECT ====="
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Deploy Spring Boot App') {
            agent { label 'worker-node' }
            steps {
                sh '''
                    APP_NAME=myapp
                    JAR_FILE=target/*.jar

                    echo "Stopping old application..."
                    pkill -f $APP_NAME || true

                    echo "Starting new application..."
                    nohup java -jar $JAR_FILE > /tmp/$APP_NAME.log 2>&1 &

                    echo "Application Deployed Successfully!"
                '''
            }
        }
    }
}
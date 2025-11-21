pipeline {
    // 1. Where the job runs (usually a dedicated Jenkins agent)
    agent any

    // 2. Define standard environment variables
    environment {
        // Customize this to match your artifact name
        ARTIFACT_NAME = 'demo-0.0.1-SNAPSHOT'
        // Define the full path to the built JAR file
        JAR_PATH = "target/${ARTIFACT_NAME}.jar"
        // Deployment target details (REPLACE THESE PLACEHOLDERS)
        DEPLOY_USER = 'jenkins-deploy'
        DEPLOY_HOST = '3.7.58.209' // Example: Your remote server IP
        DEPLOY_DIR  = '/var/www/springboot-app/' // Remote directory
    }

    // 3. Define post-build actions, even if stages fail
    post {
        always {
            // Clean up the Jenkins workspace after the build finishes
            cleanWs()
        }
        success {
            echo "Pipeline finished successfully! Deployment started."
        }
        failure {
            echo "Pipeline failed! Check the build and test stages."
        }
    }

    // 4. Define the sequential stages (steps) of the CI/CD process
    stages {

        stage('Checkout Source Code') {
            steps {
 // The pipeline automatically checks out the code from the SCM configured
                echo 'Checking out code from GitHub...'
            }
        }

        stage('Build & Unit Test') {
            steps {
                // Use Maven to clean the 'target' directory, compile, and run tests
                // Remove '-DskipTests' to enable unit tests.
                sh 'mvn clean package'
            }
        }

        stage('Vulnerability Scan (Optional)') {
            when {
                // Only run this scan on the main branch
                branch 'main'
            }
            steps {
                // Example: Integrate a security scanner (like SonarQube or OWASP Dependency Check)
                echo 'Running vulnerability and dependency checks...'
                // sh 'mvn dependency-check:check'
            }
        }

        stage('Archive Artifact') {
            steps {
                // Make the JAR file available for download on the Jenkins job page
                echo "Archiving artifact: ${JAR_PATH}"
                archiveArtifacts artifacts: "${JAR_PATH}", fingerprint: true
            }
        }

        stage('Deploy to Server') {
 steps {
                echo "Deploying ${JAR_PATH} to ${DEPLOY_HOST}..."

                // 1. Copy the JAR artifact to the remote server using SSH/SCP
                // NOTE: This assumes Jenkins has SSH credentials configured for the DEPLOY_USER
                sh "scp ${env.JAR_PATH} ${env.DEPLOY_USER}@${env.DEPLOY_HOST}:${env.DEPLOY_DIR}"

                // 2. Restart the application on the remote server
                // Example: Stop existing service, then start the new JAR
                sh "ssh ${env.DEPLOY_USER}@${env.DEPLOY_HOST} \"sudo systemctl restart springboot-app.service\""
            }
        }
    }
}

pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        CHROME_BIN = '/usr/bin/chromium'
        // Bypasses WebDriverManager's chromedriver download inside Jenkins -
        // Google's Chrome-for-Testing distribution doesn't reliably publish
        // native Linux ARM64 chromedriver builds, so we use Debian's own
        // chromium-driver package instead, which is version-matched to
        // the Chromium we installed and natively compiled for ARM64.
        CHROMEDRIVER_BIN = '/usr/bin/chromedriver'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}

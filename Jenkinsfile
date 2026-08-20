pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    // Lets a human choose Browser/Env at build time (via "Build with Parameters"
    // in Jenkins), and gives the nightly cron trigger below sensible defaults.
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests against')
        choice(name: 'ENV', choices: ['qa', 'staging', 'prod'], description: 'Target environment')
    }

    // Runs automatically every night around 2am server time, independent of
    // any code push, using the default parameter values above (chrome/qa).
    // 'H' instead of a fixed minute spreads load if many jobs share this hour.
    triggers {
        cron('H 2 * * *')
    }

    environment {
        CHROME_BIN = '/usr/bin/chromium'
        CHROMEDRIVER_BIN = '/usr/bin/chromedriver'
        FIREFOX_BIN = '/usr/bin/firefox-esr'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh "mvn clean test -Dbrowser=${params.BROWSER} -Denv=${params.ENV}"
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}

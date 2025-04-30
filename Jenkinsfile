pipeline {
    agent any
    stages {
        stage('Build All Modules') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd'
            }
        }
        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        stage('Javadoc') {
            steps {
                sh 'mvn javadoc:javadoc -Dmaven.javadoc.skip=true'
            }
        }
        stage('Site') {
            steps {
                sh 'mvn site'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar',      fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war',      fingerprint: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}

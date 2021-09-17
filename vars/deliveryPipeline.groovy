#!/usr/bin/env groovy

def call(String agent) {
    pipeline {
    agent none
    stages {
        stage('Even Stage') {
            when {
                expression { ${params.agent} == 'any' }
            }
            agent {label 'dockerworker'} 
            steps {
                echo "The build number is even"
            }
        }
        stage('Odd Stage') {
            when {
                expression { !${params.agent} == 'any' }
            }
            agent {label 'worker2'} 
            steps {
                echo "The build number is odd"
            }
        }
    }
}
}

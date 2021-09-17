#!/usr/bin/env groovy

def call(Map param){
	pipeline {
		agent {
            label "${param.agent}"
        } 
		stages {
			stage('Build') {
				when {
				expression { return "${param.agent}" == 'dockerworker'}
				}
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage('Test') {
				when {
				expression { return "${param.agent}" == 'dockerworker'}
				}
				steps {
					sh 'mvn test'
				}
				post {
					always {
						junit 'target/surefire-reports/*.xml'
					}
				}
			}
            stage('Build image') {
            when {
				expression { return "${param.agent}" == 'dockerworker'}
				}
                steps {
                    sh 'docker build -t my-app .'
                }
            }
            stage('Run app') {
            when {
				expression { return "${param.agent}" == 'dockerworker'}
				}
                steps {
                    sh 'docker run -p 8383:8383 my-app'
                }
            }
			stage('Build') {
				when {
				expression { return "${param.agent}" == 'worker2'}
				}
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage('Test') {
				when {
				expression { return "${param.agent}" == 'worker2'}
				}
				steps {
					sh 'mvn test'
				}
				post {
					always {
						junit 'target/surefire-reports/*.xml'
					}
				}
			}
			stage('Build') {
				when {
				expression { return "${param.agent}" == 'worker2'}
				}
				steps {
					sh 'java -jar target/*.jar'
				}
			}

    }
}

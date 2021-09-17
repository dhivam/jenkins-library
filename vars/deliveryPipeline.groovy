#!/usr/bin/env groovy

def call(Map param){
	pipeline {
		agent {
			label "${param.agentName}"
		}
		stages {
			stage ("Build docker") {
				when { expression { return "${param.agentName}" == 'dockerworker'} }
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage ("Test docker") {
				when { expression { return "${param.agentName}" == 'dockerworker'} }
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
				when { expression { return "${param.agentName}" == 'dockerworker'} }
				steps {
					sh 'docker build -t my-app .'
				}
			}
			stage('Run docker') {
				when { expression { return "${param.agentName}" == 'dockerworker'} }
				steps {
					sh 'docker run -p 8383:8383 my-app'
				}
			}
			stage('Build app') {
				when { expression { return "${param.agentName}" == 'worker2'} }
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage('Test app') {
				when { expression { return "${param.agentName}" == 'worker2'} }
				steps {
					sh 'mvn test'
				}
				post {
					always {
						junit 'target/surefire-reports/*.xml'
					}
				}
			}
			stage('Run app') {
				when { expression { return "${param.agentName}" == 'worker2'} }
				steps {
					sh 'java -jar target/*.jar'
				}
			}

		}
		post {
			always {
				deleteDir()
			}
    	}
    }
}

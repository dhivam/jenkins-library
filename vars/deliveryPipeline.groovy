#!/usr/bin/env groovy

def call(Map param){
	pipeline {
		agent {
			label "${param.agent}"
		}
		stages {
			stage ("Build ") {
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage ("Test") {
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
				when { expression { return "${param.agent}" == 'dockerworker'} }
				steps {
					sh 'docker build -t my-app .'
				}
			}
			stage('Run docker') {
				when { expression { return "${param.agent}" == 'dockerworker'} }
				steps {
					sh 'docker run -p 8383:8383 my-app'
				}
			}
			stage('Run app') {
				when { expression { return "${param.agent}" == 'worker2'} }
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

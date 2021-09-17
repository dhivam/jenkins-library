#!/usr/bin/env groovy


def call(Map param){
	pipeline {
		agent {
            label "${param.agent}"
        } 
		stages {
			stage ("telegram notif"){
				when {
				expression { return "${param.agent}" == 'dockerworker'}
				}
				steps{
					echo "${getMessage()} ${param.text}"
				}
			}
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
		}
        stages {
			stage ("telegram notif"){
				when {
				expression { return "${param.agent}" == 'dockerworker'}
				}
				steps{
					echo "${getMessage()} ${param.text}"
				}
			}
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
		}

    }
}

def getMessage (){
	def commiter = sh(script: "git show -s --pretty=%cn",returnStdout: true).trim()
	def message = "$commiter deploying app"
	return message
}

#!/usr/bin/env groovy


def call(Map param){
	pipeline {
		agent any 
		stages {
			stage ("telegram notif"){
				agent {
					label "dockerworker"
				}
				steps{
					echo "${getMessage()} ${param.text}"
				}
			}
			stage('Build') {
				agent {
					label "dockerworker"
				}
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage('Test') {
				agent {
					label "dockerworker"
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
			stage ("telegram notif"){
				agent {
					label "worker"
				}
				steps{
					echo "${getMessage()} ${param.text}"
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

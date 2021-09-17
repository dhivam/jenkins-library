#!/usr/bin/env groovy


def call(Map param){
	pipeline {
		agent {
			label "dockerworker"
		}
		stages {
			stage ("telegram notif"){
				steps{
					echo "${getMessage()} ${param.text}"
				}
			}
			stage('Build') {
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage('Test') {
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

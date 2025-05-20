pipeline {
    agent any
    environment {
        DEPLOYMENT_NAME = "hello-node"
        CONTAINER_NAME  = "teedy"
        IMAGE_NAME      = "wenyunxiang/teedy:latest"
    }
    stages {
        stage('Start Minikube') {
            steps {
                sh '''
                    minikube delete --all --purge
                    if ! minikube status | grep -q "Running"; then
                        echo "Starting Minikube..."
                        minikube start --driver=docker --native-ssh=false --registry-mirror=https://registry.docker-cn.com || true
                    else
                        echo "Minikube already running."
                    fi
                '''
            }
        }
        stage('Create') {
            steps {
                sh '''
                    kubectl create deployment ${DEPLOYMENT_NAME} --image=wenyunxiang/teedy:22
                '''
            }
        }
        stage('Set Image') {
            steps {
                sh '''
                    echo "Setting image for deployment..."
                    kubectl set image deployment/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME}
                '''
            }
        }
        stage('Verify') {
            steps {
                sh 'kubectl get pods'
            }
        }
    }
}

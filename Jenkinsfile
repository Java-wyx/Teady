pipeline {
    agent any
    environment {
        DEPLOYMENT_NAME = "hello-node"
        CONTAINER_NAME = "teedy"
        IMAGE_NAME = "wenyunxiang/teedy:latest"
    }
    stages {
        stage('Start Minikube') {
            steps {
                sshagent(credentials: ['minikube-ssh-credential']) {
                    sh '''
                        if ! minikube status | grep -q "Running"; then
                            echo "Starting Minikube..."
                            minikube start --registry-mirror=https://registry.docker-cn.com
                        else
                            echo "Minikube already running."
                        fi
                    '''
                }
            }
        }
        stage('Set Image') {
            steps {
                sh """
                    eval \$(minikube docker-env)
                    echo "Setting image for deployment..."
                    kubectl set image deployment/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME}
                """
            }
        }
        stage('Verify') {
            steps {
                sh "kubectl rollout status deployment/${DEPLOYMENT_NAME}"
                sh "kubectl get pods"
            }
        }
    }
}

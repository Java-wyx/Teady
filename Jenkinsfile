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
                sshagent(credentials: ['minikube-ssh-credential']) {
                    sh '''
                        set +e
                        minikube status | grep -q "Running"
                        if [ $? -ne 0 ]; then
                            echo "Minikube not running or status check failed, starting..."
                            minikube start --driver=docker --registry-mirror=https://registry.docker-cn.com || true
                        else
                            echo "Minikube already running."
                        fi
                        set -e
                    '''
                }
            }
        }
        stage('Set Image') {
            steps {
                sshagent(credentials: ['minikube-ssh-credential']) {
                    sh '''
                        set +e
                        eval $(minikube docker-env) || true
                        echo "Setting image for deployment..."
                        kubectl set image deployment/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME}
                        set -e
                    '''
                }
            }
        }
        stage('Verify') {
            steps {
                sshagent(credentials: ['minikube-ssh-credential']) {
                    sh "kubectl rollout status deployment/${DEPLOYMENT_NAME}"
                    sh "kubectl get pods"
                }
            }
        }
    }
}

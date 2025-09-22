@Library("Shared-Libraries@main") _

pipeline {
    agent any

    environment {
        IMAGE_NAME = "jaink310/django-backend"
        IMAGE_TAG  = "v2"
        KUBE_FOLDER = "kubernetes"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker_build(
                        imageName: "${IMAGE_NAME}",
                        imageTag: "${IMAGE_TAG}",
                        dockerfile: "Dockerfile",
                        context: "."
                    )
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker_push(
                        imageName: "${IMAGE_NAME}",
                        imageTag: "${IMAGE_TAG}",
                        credentials: "docker-hub-credentials"
                    )
                }
            }
        }

        

        stage('Install kubectl') {
            steps {
                sh '''
                    if ! command -v kubectl &> /dev/null
                    then
                        echo "kubectl not found, installing..."
                        curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                        chmod +x kubectl
                        sudo mv kubectl /usr/local/bin/
                    else
                        echo "kubectl already installed"
                    fi
                '''
            }
        }

        stage('Debug Kubernetes') {
            steps {
                script {
                    sh """
                        echo "==== Kubectl Version ===="
                        kubectl version --client
                        echo "==== Current Context ===="
                        kubectl config current-context || echo "No current context"
                        echo "==== Get Nodes ===="
                        kubectl get nodes || echo "Cannot connect to cluster"
                        echo "==== Get Namespaces ===="
                        kubectl get ns || echo "Cannot connect to cluster"
                    """
                }
            }
        }

        // stage('Apply Kubernetes Manifests') {
        //     steps {
        //         script {
        //             echo "Applying Kubernetes manifests from folder: ${KUBE_FOLDER}"
        //             sh """
        //                 kubectl apply --validate=false -f ${KUBE_FOLDER}/*.yaml || echo "Failed to apply manifests"
        //             """
        //         }
        //     }
        // }

        stage('Apply Kubernetes Manifests') {
    steps {
        script {
            echo "Applying Kubernetes manifests from folder: ${KUBE_FOLDER}"
            sh """
                kubectl apply --validate=false -f ${KUBE_FOLDER}/ || echo "Failed to apply manifests"
            """
        }
    }
}

    stage('Run Docker Container') {
            steps {
                script {
                    sh """
                        docker pull ${IMAGE_NAME}:${IMAGE_TAG}
                        docker stop django-backend || true
                        docker rm django-backend || true
                        docker run -d -p 8000:8000 --name django-backend ${IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Docker container running on port 8000 and Kubernetes manifests applied (if cluster accessible)"
            }
        }
    }
}












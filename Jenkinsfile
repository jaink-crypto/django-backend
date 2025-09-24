// @Library("Shared-Libraries@main") _

// pipeline {
//     agent any

//     environment {
//         IMAGE_NAME = "jaink310/django-backend"
//         IMAGE_TAG  = "v2"
//         KUBE_FOLDER = "kubernetes"
//     }

//     stages {
//         stage('Checkout') {
//             steps {
//                 checkout scm
//             }
//         }

//         stage('Build Docker Image') {
//             steps {
//                 script {
//                     docker_build(
//                         imageName: "${IMAGE_NAME}",
//                         imageTag: "${IMAGE_TAG}",
//                         dockerfile: "Dockerfile",
//                         context: "."
//                     )
//                 }
//             }
//         }

//         stage('Push Docker Image') {
//             steps {
//                 script {
//                     docker_push(
//                         imageName: "${IMAGE_NAME}",
//                         imageTag: "${IMAGE_TAG}",
//                         credentials: "docker-hub-credentials"
//                     )
//                 }
//             }
//         }

        

//         stage('Install kubectl') {
//             steps {
//                 sh '''
//                     if ! command -v kubectl &> /dev/null
//                     then
//                         echo "kubectl not found, installing..."
//                         curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
//                         chmod +x kubectl
//                         sudo mv kubectl /usr/local/bin/
//                     else
//                         echo "kubectl already installed"
//                     fi
//                 '''
//             }
//         }

//         stage('Debug Kubernetes') {
//             steps {
//                 script {
//                     sh """
//                         echo "==== Kubectl Version ===="
//                         kubectl version --client
//                         echo "==== Current Context ===="
//                         kubectl config current-context || echo "No current context"
//                         echo "==== Get Nodes ===="
//                         kubectl get nodes || echo "Cannot connect to cluster"
//                         echo "==== Get Namespaces ===="
//                         kubectl get ns || echo "Cannot connect to cluster"
//                     """
//                 }
//             }
//         }

//         // stage('Apply Kubernetes Manifests') {
//         //     steps {
//         //         script {
//         //             echo "Applying Kubernetes manifests from folder: ${KUBE_FOLDER}"
//         //             sh """
//         //                 kubectl apply --validate=false -f ${KUBE_FOLDER}/*.yaml || echo "Failed to apply manifests"
//         //             """
//         //         }
//         //     }
//         // }

//         stage('Apply Kubernetes Manifests') {
//     steps {
//         script {
//             echo "Applying Kubernetes manifests from folder: ${KUBE_FOLDER}"
//             sh """
//                 kubectl apply --validate=false -f ${KUBE_FOLDER}/ || echo "Failed to apply manifests"
//             """
//         }
//     }
// }

//     // stage('Run Docker Container') {
//     //         steps {
//     //             script {
//     //                 sh """
//     //                     docker pull ${IMAGE_NAME}:${IMAGE_TAG}
//     //                     docker stop django-backend || true
//     //                     docker rm django-backend || true
//     //                     docker run -d -p 8000:8000 --name django-backend ${IMAGE_NAME}:${IMAGE_TAG}
//     //                 """
//     //             }
//     //         }
//     //     }
        
//         stage('Deploy') {
//             steps {
//                 echo "Docker container running on port 8000 and Kubernetes manifests applied (if cluster accessible)"
//             }
//         }
//     }
// }



@Library("Shared-Libraries@main") _

pipeline {
    agent any

    environment {
        IMAGE_NAME  = "jaink310/django-backend"
        IMAGE_TAG   = "v2"
        KUBE_FOLDER = "kubernetes"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
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

        stage('Apply Kubernetes Manifests') {
            steps {
                script {
                    echo "Applying Kubernetes manifests from folder: ${KUBE_FOLDER}"
                    sh """
                        kubectl apply --validate=false -f ${KUBE_FOLDER}/ || echo "Failed to apply manifests"
                        
                        # Force pods to restart so new image is pulled
                        kubectl rollout restart deployment django-backend-deployment -n django-backend
                        
                        # Wait until pods are ready
                        kubectl rollout status deployment django-backend-deployment -n django-backend
                    """
                }
            }
        }

        // stage('Deploy') {
        //     steps {
        //         echo "Deployment done: Kubernetes manifests applied and pods restarted to pull latest image."
        //     }
        // }

        stage('Deploy') {
            steps {
                echo "Deployment done: Kubernetes manifests applied and pods restarted to pull latest image."
                script {
                    sh """
                        # Kill any previous port-forward processes (optional)
                        pkill -f 'kubectl port-forward -n django-backend service/django-backend-service'
        
                        # Start port-forward in detached mode so EC2 public IP can access the service
                        nohup kubectl port-forward -n django-backend service/django-backend-service 8000:80 --address 0.0.0.0 > port-forward.log 2>&1 &
                        
                        echo "Port-forward started in background. Access service at http://<EC2-PUBLIC-IP>:8000"
                    """
                }
            }
}
    }
}









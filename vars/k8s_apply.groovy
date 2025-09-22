def call(Map config = [:]) {
    // Get folder path from config, default to 'kubernetes'
    def folder = config.folder ?: 'kubernetes'

    echo "Applying Kubernetes manifests from folder: ${folder}"

    // Apply all YAML files in the folder
    sh """
        for file in ${folder}/*.yaml; do
            echo "Applying \$file"
            kubectl apply -f \$file
        done
    """
}
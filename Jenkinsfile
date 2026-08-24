pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        DOCKER_REGISTRY = "docker.io"
        IMAGE_NAME = "axandrade/trampohub-api"
        IMAGE_TAG = "${BUILD_NUMBER}"
        MONGO_HOST = "host.docker.internal"
        RABBITMQ_HOST = "host.docker.internal"
        SPRING_PORT = "8001"
        CONTAINER_PORT = "8081"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "📦 Fazendo checkout do repositório..."
                checkout scm
            }
        }

        stage('Build Maven') {
            steps {
                echo '🔨 Compilando com Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Rodando testes unitários...'
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construindo imagem Docker...'
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${IMAGE_TAG} \
                      -t ${IMAGE_NAME}:latest \
                      .
                '''
            }
        }

        stage('Deploy Homolog') {
            when {
                branch 'develop'
            }
            steps {
                echo '🚀 Fazendo deploy em homolog...'
                sh '''
                    echo "=== Parando container anterior ==="
                    docker stop trampohub-api-homolog || true
                    docker rm trampohub-api-homolog || true

                    echo "=== Iniciando novo container ==="
                    docker run -d \
                      --name trampohub-api-homolog \
                      -p ${SPRING_PORT}:${CONTAINER_PORT} \
                      -e SPRING_PROFILES_ACTIVE=homolog \
                      -e SERVER_PORT=${CONTAINER_PORT} \
                      -e SPRING_DATA_MONGODB_URI=mongodb://admin:trampohub123@${MONGO_HOST}:27017/trampohub?authSource=admin \
                      -e SPRING_RABBITMQ_HOST=${RABBITMQ_HOST} \
                      -e SPRING_RABBITMQ_PORT=5672 \
                      -e SPRING_RABBITMQ_USERNAME=guest \
                      -e SPRING_RABBITMQ_PASSWORD=guest \
                      --restart unless-stopped \
                      ${IMAGE_NAME}:${IMAGE_TAG}

                    sleep 5
                    docker logs trampohub-api-homolog

                    echo ""
                    echo "✅ API: http://localhost:${SPRING_PORT}/api"
                    echo "✅ Health: http://localhost:${SPRING_PORT}/api/actuator/health"
                '''
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f --filter "dangling=true" || true'
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }

        success {
            echo '✅ Pipeline executado com sucesso!'
        }

        failure {
            echo '❌ Pipeline falhou!'
            sh 'docker logs trampohub-api-homolog || echo "Container não está rodando"'
        }
    }
}
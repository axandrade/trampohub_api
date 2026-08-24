pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        IMAGE_NAME     = "trampohub-api"
        IMAGE_TAG      = "${BUILD_NUMBER}"
        APP_PORT       = "8001"
        DOCKER_NETWORK = "trampohub-net"
        MONGO_URI      = "mongodb://admin:trampohub123@mongo-homolog:27017/trampohub_java_homolog?authSource=admin"
    }

    stages {
        stage('Build Maven') {
            steps {
                echo 'Compilando com Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Rodando testes...'
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Construindo imagem Docker...'
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
                echo 'Fazendo deploy em homolog...'
                sh '''
                    docker stop trampohub-api-homolog || true
                    docker rm trampohub-api-homolog || true

                    docker run -d \
                      --name trampohub-api-homolog \
                      --network ${DOCKER_NETWORK} \
                      -p ${APP_PORT}:${APP_PORT} \
                      -e SPRING_PROFILES_ACTIVE=homolog \
                      -e SERVER_PORT=${APP_PORT} \
                      -e SPRING_DATA_MONGODB_URI=${MONGO_URI} \
                      -e SPRING_RABBITMQ_HOST=rabbitmq-homolog \
                      -e SPRING_RABBITMQ_PORT=5672 \
                      -e SPRING_RABBITMQ_USERNAME=guest \
                      -e SPRING_RABBITMQ_PASSWORD=guest \
                      --restart unless-stopped \
                      ${IMAGE_NAME}:${IMAGE_TAG}

                    sleep 10
                    docker logs --tail 50 trampohub-api-homolog
                '''
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            sh 'docker image prune -f --filter "dangling=true" || true'
        }

        success {
            echo "Deploy concluído — http://localhost:${APP_PORT}"
        }

        failure {
            echo 'Pipeline falhou!'
            sh 'docker logs --tail 100 trampohub-api-homolog || echo "Container não está rodando"'
        }
    }
}
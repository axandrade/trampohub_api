pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo '🔨 Compilando com Maven...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construindo imagem Docker...'
                bat 'docker build -t trampohub-api:${BUILD_NUMBER} .'
            }
        }

        stage('Deploy Homolog') {
            when {
                branch 'develop'
            }
            steps {
                echo '🚀 Fazendo deploy em homolog...'
                bat '''
                    docker stop trampohub-api-homolog || exit /b 0
                    docker rm trampohub-api-homolog || exit /b 0
                    docker run -d ^
                      --name trampohub-api-homolog ^
                      -p 8001:8001 ^
                      -e SPRING_PROFILES_ACTIVE=homolog ^
                      -e MONGO_HOST=host.docker.internal ^
                      -e MONGO_PASSWORD=trampohub123 ^
                      -e RABBITMQ_HOST=host.docker.internal ^
                      trampohub-api:%BUILD_NUMBER%
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Deploy bem-sucedido!'
        }
        failure {
            echo '❌ Deploy falhou!'
        }
    }
}
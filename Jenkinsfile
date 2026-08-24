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
                bat '''
                    mvn clean package -DskipTests ^
                        -Dmaven.test.skip=true ^
                        -Dorg.slf4j.simpleLogger.defaultLogLevel=info
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Rodando testes unitários...'
                bat '''
                    mvn test ^
                        -Dorg.slf4j.simpleLogger.defaultLogLevel=info
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construindo imagem Docker...'
                bat '''
                    docker build ^
                      --build-arg BUILD_NUMBER=%BUILD_NUMBER% ^
                      -t %IMAGE_NAME%:%IMAGE_TAG% ^
                      -t %IMAGE_NAME%:latest ^
                      .
                '''
                script {
                    echo "✅ Imagem construída: ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                branch 'develop'
            }
            steps {
                echo '🔍 Analisando código com SonarQube...'
                bat '''
                    mvn sonar:sonar ^
                      -Dsonar.projectKey=trampohub-api ^
                      -Dsonar.sources=src ^
                      -Dsonar.host.url=http://localhost:9000 ^
                      -Dsonar.login=%SONARQUBE_TOKEN%
                '''
            }
        }

        stage('Deploy Homolog') {
            when {
                branch 'develop'
            }
            steps {
                echo '🚀 Fazendo deploy em homolog...'
                bat '''
                    echo Parando container anterior...
                    for /f %%i in ('docker ps -q -f name=trampohub-api-homolog') do docker stop %%i
                    for /f %%i in ('docker ps -aq -f name=trampohub-api-homolog') do docker rm %%i

                    echo Iniciando novo container...
                    docker run -d ^
                      --name trampohub-api-homolog ^
                      -p %SPRING_PORT%:8080 ^
                      -e SPRING_PROFILES_ACTIVE=homolog ^
                      -e SPRING_DATA_MONGODB_URI=mongodb://admin:trampohub123@%MONGO_HOST%:27017/trampohub?authSource=admin ^
                      -e SPRING_RABBITMQ_HOST=%RABBITMQ_HOST% ^
                      -e SPRING_RABBITMQ_PORT=5672 ^
                      -e SPRING_RABBITMQ_USERNAME=guest ^
                      -e SPRING_RABBITMQ_PASSWORD=guest ^
                      -e JAVA_OPTS=-Xmx512m -Xms256m ^
                      --restart unless-stopped ^
                      %IMAGE_NAME%:%IMAGE_TAG%

                    timeout /t 5 /nobreak
                    echo Verificando saúde do container...
                    docker logs trampohub-api-homolog
                '''
            }
        }

        stage('Deploy Production') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Fazendo deploy em PRODUÇÃO...'
                input 'Deseja fazer deploy em PRODUÇÃO?'

                bat '''
                    echo Pushing imagem para registry...
                    docker tag %IMAGE_NAME%:%IMAGE_TAG% %DOCKER_REGISTRY%/%IMAGE_NAME%:%IMAGE_TAG%
                    docker tag %IMAGE_NAME%:latest %DOCKER_REGISTRY%/%IMAGE_NAME%:latest

                    REM Fazer login e push (se usar registry privado)
                    REM docker login -u %DOCKER_USER% -p %DOCKER_PASSWORD%
                    REM docker push %DOCKER_REGISTRY%/%IMAGE_NAME%:%IMAGE_TAG%
                    REM docker push %DOCKER_REGISTRY%/%IMAGE_NAME%:latest
                '''
            }
        }
    }

    post {
        always {
            echo '🧹 Limpando recursos...'
            bat '''
                echo Exibindo logs do container...
                docker logs trampohub-api-homolog || echo "Container não existe ou já foi removido"

                echo Limpando imagens dangling...
                docker image prune -f --filter "dangling=true"
            '''

            junit '**/target/surefire-reports/*.xml', allowEmptyResults: true
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site',
                reportFiles: 'index.html',
                reportName: 'Test Report'
            ])
        }

        success {
            echo '✅ Pipeline executado com sucesso!'
            bat 'docker ps -f name=trampohub-api-homolog --format="table {{.Names}}\t{{.Status}}\t{{.Ports}}"'
        }

        failure {
            echo '❌ Pipeline falhou!'
            bat 'docker logs trampohub-api-homolog'
        }

        unstable {
            echo '⚠️ Pipeline instável (testes falharam)'
        }
    }
}
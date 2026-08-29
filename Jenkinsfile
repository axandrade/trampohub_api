pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        IMAGE_NAME = "trampohub-api"
        IMAGE_TAG  = "${BUILD_NUMBER}"
        APP_PORT   = "8001"
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

        stage('SonarQube Analysis') {
            steps {
                echo 'Analisando qualidade do código com SonarQube...'
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('SonarQube Local') {
                        sh 'mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN}'
                    }
                }
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
            steps {
                echo 'Fazendo deploy em homolog...'
                sh '''
                    docker stop trampohub-api-homolog || true
                    docker rm trampohub-api-homolog || true

                    docker run -d \
                      --name trampohub-api-homolog \
                      --add-host=host.docker.internal:host-gateway \
                      -p ${APP_PORT}:${APP_PORT} \
                      -e SPRING_PROFILES_ACTIVE=homolog \
                      -e MONGO_HOST=host.docker.internal \
                      -e MONGO_PASSWORD=trampohub123 \
                      -e RABBITMQ_HOST=host.docker.internal \
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
            echo "Deploy concluído — http://localhost:8001"
        }

        failure {
            echo 'Pipeline falhou!'
            sh 'docker logs --tail 100 trampohub-api-homolog || echo "Container não está rodando"'
        }
    }
}
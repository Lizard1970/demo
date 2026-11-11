pipeline {
    agent any
    environment {
        HARBOR_ADDR = "192.168.139.30:5000"
        IMAGE_REPO = "${HARBOR_ADDR}/demo/test"
        TAG = "v${BUILD_NUMBER}"
        FULL_IMAGE = "${IMAGE_REPO}:${TAG}"
    }
    stages {
        stage('Maven打包-JDK21') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21'
                    args '-v $HOME/.m2:/root/.m2 -v /etc/localtime:/etc/localtime'
                }
            }
            steps {
                sh 'mvn clean package -DskipTests -s settings.xml'
            }
        }
        stage('构建镜像并推送Harbor') {
            steps {
                sh """
                docker login ${HARBOR_ADDR} -u admin -p Harbor@12345
                docker build -t ${FULL_IMAGE} .
                docker push ${FULL_IMAGE}
                """
            }
        }
        stage('部署到K8s') {
            steps {
                sh """
                # 替换yaml里镜像tag，然后应用
                sed -i 's#image:.*#image: ${FULL_IMAGE}#' deployment.yaml
                kubectl apply -f deployment.yaml
                """
            }
        }
    }
    post {
        success { echo "✅ CICD流水线全部成功，镜像：${FULL_IMAGE}" }
        failure { echo "❌ 流水线失败！" }
    }
}

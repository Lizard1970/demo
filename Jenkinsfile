pipeline {
    agent any
    environment {
        HARBOR_ADDR = "192.168.139.30:5000"
        HARBOR_REPO = "demo"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        FULL_IMAGE = "${HARBOR_ADDR}/${HARBOR_REPO}:${IMAGE_TAG}"
    }
    stages {
        stage('拉取代码') {
            steps {
                git url: 'https://github.com/Lizard1970/demo.git', branch: 'master'
            }
        }
        stage('Maven打包') {
            steps {
                sh 'mvn clean package -DskipTests -Dmaven.test.skip=true'
            }
        }
        stage('构建镜像+推送Harbor') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'harbor-auth', passwordVariable: 'HARBOR_PWD', usernameVariable: 'HARBOR_USER')]) {
                    sh """
                        docker login ${HARBOR_ADDR} -u ${HARBOR_USER} -p ${HARBOR_PWD}
                        docker build -t ${FULL_IMAGE} .
                        docker push ${FULL_IMAGE}
                        docker logout ${HARBOR_ADDR}
                    """
                }
            }
        }
        stage('部署K8s') {
            steps {
                sh "kubectl set image deployment/demo-deploy demo=${FULL_IMAGE} -n default"
            }
        }
    }
    post {
        success {
            echo "✅ 流水线成功，镜像：${FULL_IMAGE}"
        }
        failure {
            echo "❌ 流水线执行失败"
        }
    }
}

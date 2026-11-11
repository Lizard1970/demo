pipeline {
    agent any
    environment {
        HARBOR_ADDR = "192.168.139.30:5000"
        IMAGE_REPO = "${HARBOR_ADDR}/demo/test"
        TAG = "v${BUILD_NUMBER}"
        FULL_IMAGE = "${IMAGE_REPO}:${TAG}"
        // 宿主机harbor-nfs
        DOCKER_HOST="192.168.139.30"
    }


    stages {
        stage('同步代码到harbor-nfs') {
                steps {
                    sh '''
                        tar -czf - --exclude=.git -C . . | ssh root@192.168.139.30 "rm -rf /root/demo && mkdir -p /root/demo && tar -xzf - -C /root/demo"
                    '''
                }
            }

        stage('Maven打包-JDK21（ssh到harbor-nfs执行）') {
            steps {
                sh """
                ssh root@${DOCKER_HOST} "
                cd /root/demo &&
                mvn clean package -DskipTests
                "
                """
            }
        }
        stage('构建镜像推送Harbor（ssh到harbor-nfs）') {
            steps {
                sh """
                ssh root@${DOCKER_HOST} "
                cd /root/demo &&
                docker login ${HARBOR_ADDR} -u admin -p Harbor@12345 &&
                docker build -t ${FULL_IMAGE} . &&
                docker push ${FULL_IMAGE}
                "
                """
            }
        }
        stage('部署K8s（ssh到master）') {
            steps {
                sh """
                ssh root@192.168.139.20 "
                cd /root/demo &&
                sed -i 's#image:.*#image: ${FULL_IMAGE}#' deployment.yaml &&
                kubectl apply -f deployment.yaml
                "
                """
            }
        }
    }
    post {
        success { echo "✅ CICD流水线全部成功，镜像：${FULL_IMAGE}" }
        failure { echo "❌ 流水线失败！" }
    }
}

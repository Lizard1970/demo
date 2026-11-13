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
                JAVA_HOME=/opt/java/openjdk /opt/apache-maven-3.9.16/bin/mvn clean package -DskipTests
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
                # 先把仓库里的deployment.yaml传到master
                ssh root@192.168.139.20 "mkdir -p /root/demo"
                scp deployment.yaml root@192.168.139.20:/root/demo/

                #替换镜像tag并部署
                ssh root@192.168.139.20 "
                cd /root/demo &&
                sed -i 's#image:.*#image: ${FULL_IMAGE}#' deployment.yml &&
                kubectl apply -f deployment.yml
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

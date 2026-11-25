pipeline {
    agent any
    environment {
        HARBOR_ADDR = "192.168.139.30:5000"
        IMAGE_REPO = "${HARBOR_ADDR}/demo/test"
        TAG = "v${BUILD_NUMBER}"
        FULL_IMAGE = "${IMAGE_REPO}:${TAG}"
        // 宿主机harbor-nfs
        DOCKER_HOST="192.168.139.30"
        WECHAT_WEBHOOK = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=9f207fb3-8477-4a2e-a293-2f56e86b091b"
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
                    scp k8s/deployment.yml root@192.168.139.20:/root/demo/
                    scp k8s/service.yml root@192.168.139.20:/root/demo/
                    scp k8s/ingress.yml root@192.168.139.20:/root/demo/


                    #替换镜像tag并部署
                    ssh root@192.168.139.20 "
                        cd /root/demo &&
                        sed -i 's#image:.*#image: ${FULL_IMAGE}#' deployment.yml &&
                        kubectl apply -f deployment.yml
                        kubectl apply -f service.yml
                        kubectl apply -f ingress.yml
                    "
                """
            }
        }
    }
    post {
        success {
            echo "✅ CICD流水线全部成功，镜像：${FULL_IMAGE}"
            sh """
                curl ${WECHAT_WEBHOOK} \
                -H "Content-Type: application/json" \
                -d '{
                    "msgtype": "markdown",
                    "markdown": {
                        "content": "✅ demo应用发布成功\n> 构建号：${BUILD_NUMBER}\n> 镜像地址：${FULL_IMAGE}\n> 任务地址：${BUILD_URL}"
                    }
                }'
            """
        }
        failure {
            echo "❌ 流水线失败！"
            sh """
                curl ${WECHAT_WEBHOOK} \
                -H "Content-Type: application/json" \
                -d '{
                    "msgtype": "markdown",
                    "markdown": {
                        "content": "❌ demo应用发布失败\n> 构建号：${BUILD_NUMBER}\n> 镜像地址：${FULL_IMAGE}\n> 任务地址：${BUILD_URL}"
                    }
                }'
            """
        }
    }
}

pipeline {
    agent any
    environment {
        HARBOR_ADDR = "192.168.139.30:5000"
        IMAGE_REPO = "${HARBOR_ADDR}/demo/test"
        TAG = "v${BUILD_NUMBER}"
        FULL_IMAGE = "${IMAGE_REPO}:${TAG}"
        DOCKER_HOST = "192.168.139.30"
        WECHAT_WEBHOOK = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=9f207fb3-8477-4a2e-a293-2f56e86b091b"
        // 从jenkins凭证读取harbor账号密码，不写明文
        HARBOR_CREDS = credentials('harbor-cred')
        HARBOR_USER = HARBOR_CREDS.split(':')[0]
        HARBOR_PWD = HARBOR_CREDS.split(':')[1]
    }
    stages {
        stage('同步代码到harbor-nfs') {
            steps {
                sh '''
                    set -e
                    tar -czf - --exclude=.git -C . . | ssh root@${DOCKER_HOST} "rm -rf /root/demo && mkdir -p /root/demo && tar -xzf - -C /root/demo"
                '''
            }
        }

        stage('Maven打包-JDK21（harbor-nfs）') {
            steps {
                sh """
                set -e
                ssh root@${DOCKER_HOST} "
                    cd /root/demo
                    JAVA_HOME=/opt/java/openjdk /opt/apache-maven-3.9.16/bin/mvn clean package -DskipTests
                "
                """
            }
        }

        stage('构建&推送镜像到Harbor') {
            steps {
                // 凭证ID：harbor-cred，类型 Username with password
                withCredentials([usernamePassword(credentialsId: 'harbor-cred', passwordVariable: 'HPWD', usernameVariable: 'HUSER')]) {
                    sh """
                    set -e
                    ssh root@${DOCKER_HOST} "
                        cd /root/demo
                        docker login ${HARBOR_ADDR} -u ${HUSER} -p ${HPWD}
                        docker build -t ${FULL_IMAGE} .
                        docker push ${FULL_IMAGE}
                        docker logout ${HARBOR_ADDR}
                    "
                    """
                }
            }
        }

        stage('部署K8s（master节点）') {
            steps {
                sh """
                set -e
                ssh root@192.168.139.20 "
                    cd /root/demo
                    # 精准替换deployment里容器镜像，只匹配spec.template.spec.containers[0].image
                    # 用yq 【推荐】，如果集群没有yq，我给你sed版本在下面
                    yq eval '.spec.template.spec.containers[0].image = "${FULL_IMAGE}"' -i all-in-one.yaml
                    kubectl apply -f all-in-one.yaml
                    # 可选：等待pod就绪
                    kubectl rollout status deployment/deployment-name -w --timeout=120s
                "
                """
            }
        }
    }

    post {
        success {
            script {
                def msg = """✅ **demo应用发布成功**
> 构建号：${BUILD_NUMBER}
> 镜像地址：${FULL_IMAGE}
> 任务地址：${BUILD_URL}"""
                sh """
                curl ${WECHAT_WEBHOOK} \
                -H "Content-Type: application/json" \
                -d '{
                    "msgtype": "markdown",
                    "markdown": {
                        "content": ${msg.replace('"','\\"').replace('\n','\\n')}
                    }
                }'
                """
            }
        }
        failure {
            script {
                def msg = """❌ **demo应用发布失败**
> 构建号：${BUILD_NUMBER}
> 镜像地址：${FULL_IMAGE}
> 任务地址：${BUILD_URL}"""
                sh """
                curl -s ${WECHAT_WEBHOOK} \
                -H "Content-Type: application/json" \
                -d '{
                    "msgtype": "markdown",
                    "markdown": {
                        "content": "${escapeMsg}"
                    }
                }'
                """
            }
        }
    }
}

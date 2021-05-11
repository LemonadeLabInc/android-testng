def versionFromProperties() {
    node {
        checkout scm
        def props = readProperties(file: 'gradle.properties')
        return props.version
    }
}

@Library('leomo-android') _
pipeline {
    agent any
    environment {
        API_LEVEL = 'all'
        BUILD_ID = leomoSetupBuildId()
        S3_ACCESS_KEY = credentials('leomo-library-s3-access-key')
        S3_SECRET_KEY = credentials('leomo-library-s3-secret-key')
        S3_REPOSITORY = credentials('leomo-library-s3-repository')
        VERSION = versionFromProperties()
    }
    stages {
        stage('Deploy') {
            when {
                anyOf {
                    branch 'master'
                    branch 'hotfix/*'
                    branch 'release/*'
                    changeRequest branch: 'hotfix/*', comparator: 'GLOB'
                    changeRequest branch: 'release/*', comparator: 'GLOB'
                }
            }
            steps {
                leomoAndroidBuild(apiLevel: env.API_LEVEL,  {
                    sh "./gradlew -PbuildNumber=${env.BUILD_ID} clean publish uploadS3 -x javadocRelease -Ps3.accessKey=${env.S3_ACCESS_KEY} -Ps3.secretKey=${env.S3_SECRET_KEY}"
                })
                leomoTag "${env.VERSION}.${env.BUILD_ID}", "Jenkins Build ${env.BUILD_DISPLAY_NAME}\nSee ${env.BUILD_URL}"
            }
        }
    }
    post {
        always {
            leomoBuildFinished()
        }
    }
}
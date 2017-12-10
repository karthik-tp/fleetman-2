node {
   stage('Preparation') { 
      git 'https://github.com/karthik-tp/fleetman-2'
   }
   stage('Build') {
  
         sh "mvn clean package"
      
   }
   stage('Results') {
      archive 'target/*.war'
   }
   stage('Deploy')
   {
      withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) 
      {
         ansiblePlaybook credentialsId: 'ssh-agent', installation: 'ansible-installation', playbook: 'deploy.yaml', sudoUser: null
      }
   }
}

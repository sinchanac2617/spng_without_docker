### Procedure to deploy springboot application using CICD without docker file
Take 2 Servers: 1.Jenkins  (AMI as ubuntu 22.04 lts and instance type as 2cpu ram and 4gb)  
                2.App      (AMI as ubuntu 22.04 lts and instance type as 2cpu ram and 4gb) 
And connect to mobaxterm
Install java in both server in ubuntu user
Install jenkins in jenkin sever in ubuntu user
create jenkin user in jenkin server and deploy user in app server
 ## In app server create deploy user as ubuntu user
 # [App Server][as ubuntu user]
               sudo adduser deploy
               sudo usermod -aG sudo deploy
## Create folder for app and give to deploy
# [App Server][ubuntu]
               sudo mkdir -p /opt/myapp
               sudo chown deploy:deploy /opt/myapp
## Create systemd service
# [App Server][ubuntu]
               sudo nano /etc/systemd/system/myapp.service
# and paste this inside
              [Unit]
             Description=My Spring Boot Application
             After=network.target

             [Service]
             User=deploy
             WorkingDirectory=/opt/myapp
             ExecStart=/usr/bin/java -jar /opt/myapp/app.jar
             SuccessExitStatus=143
             Restart=always
             RestartSec=10

            [Install]
            WantedBy=multi-user.target
# save and exit
## Reload systemd:
# [App Server][ubuntu]
          sudo systemctl daemon-reload
## Configure sudo (visudo) for deploy user
# [App Server][ubuntu]
         sudo visudo
# At the bottom, add:
         deploy ALL=(ALL) NOPASSWD: /usr/bin/systemctl restart myapp.service, /usr/bin/systemctl status myapp.service --no-pager
## Test sudo as deploy
# [App Server][ubuntu]
         su - deploy

# [App Server][deploy]
        sudo -n /usr/bin/systemctl status myapp.service --no-pager
## Setup Jenkins Server
# [Jenkins Server][as ubuntu user] to install jenkins
      curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | \
      sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null

      echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
      https://pkg.jenkins.io/debian-stable binary/ | \
      sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

     sudo apt update
     sudo apt install -y jenkins
## Start Jenkins:
# [Jenkins Server][ubuntu]
    sudo systemctl enable jenkins
    sudo systemctl start jenkins
    sudo systemctl status jenkins
## Setup SSH key: Jenkins → App (jenkins → deploy)
# switch to jenkins user
# [Jenkins Server][ubuntu]
    sudo su - jenkins
## Generate SSH key
# [Jenkins Server][as jenkins user]
    ssh-keygen -t rsa -b 4096 -C "jenkins-deploy" -f ~/.ssh/id_rsa -N ""
## to show the public key:
# [Jenkins Server][jenkins]
    cat ~/.ssh/id_rsa.pub
## Add key to deploy’s authorized_keys on App server
# Back on App Server terminal:
# [App Server][ubuntu]
    su - deploy

# [App Server][deploy]
    mkdir -p ~/.ssh
    chmod 700 ~/.ssh
    nano ~/.ssh/authorized_keys
## Paste the public key, save, then:
# [App Server][deploy]
    chmod 600 ~/.ssh/authorized_keys

## Test SSH
# From Jenkins Server as jenkins:
# [Jenkins Server][jenkins]
    ssh deploy@<APP_SERVER_private_IP> -o StrictHostKeyChecking=no
# If you log in without password, it’s working.

# exit to go back as needed.
## Prepare your Spring Boot project
# build locally inside the project folder using
     mvn clean package -DskipTests
## Create Jenkinsfile in GitHub repo
jenkinsfile script is in repo
## Configure Jenkins job (in browser)
Unlock Jenkins

Browser → http://<JENKINS_SERVER_PUBLIC_IP>:8080

On Jenkins Server:
# [Jenkins Server][as ubuntu user]
     sudo cat /var/lib/jenkins/secrets/initialAdminPassword 
# once logged in 
## Create Pipeline job

Click “New Item”

Name: springboot-ci-cd

Type: Pipeline → OK

In Pipeline section:

Definition: Pipeline script from SCM

SCM: Git

Repository URL: https://github.com/<your-username>/<your-repo>.git

Branch: */main (or whatever)

Script Path: Jenkinsfile

Save.

## Verify on App server
# On App Server as ubuntu:
     sudo systemctl status myapp.service --no-pager
# Should show: active (running).

## finally build the project through jenkins and paste the public ip of app server with :port number ex: public ip:8080

Here is a look at the jenkins full stage build view dashboard:

![A screenshot of the main project dashboard](images/sp2.png)


Here is a look at the application dashboard:

![A screenshot of the main project dashboard](images/sp1.png)

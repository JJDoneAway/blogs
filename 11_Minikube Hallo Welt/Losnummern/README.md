# Getting Started

## Guides to get started with SpringBoot
The following guides illustrates how to use certain features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

## Guide to set up a MiniKube environment on your Windows machine
1. check if kubectl is already installed. if not do so
2. install choco via PowerShel in Admin mode `Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))`
3. install minikube via choco (https://kubernetes.io/docs/tasks/tools/install-minikube/#windows) `choco install -y minikube kubernetes-cli`
4. install "External vswitch" to your hyperv (https://docs.docker.com/machine/drivers/hyper-v/). I named it "minikube_switch". Min to use a real network card like your ethernet
5. Reboot
6. delete if exist the ~/.ninikube folder with all old settings

## start Minikube
1. `minikube start --vm-driver hyperv --hyperv-virtual-switch "minikube_switch" --disk-size 10g --memory 4096 --v 9999 --alsologtostderr`
2. `minikube dashboard`
3. check context of your kubectl `kubectl config get-contexts`
4. in case change context `kubectl config use-context minikube` 
5. add secret for your docker hub if not already done `kubectl create secret docker-registry regcred --docker-server=https://index.docker.io/v1/ --docker-username=hoehne --docker-password=<your docker hub password> --docker-email=hoehne@magic-inside.de`


## Building steps
1. build spring boot project: `mvn install`
2. build docker image: `docker build --tag hoehne/number-generator:Losnummern .`
3. push docker image to the registry: `docker push hoehne/number-generator:Losnummern`
4. install deployment to minikube `kubectl apply -f k8s-deployment.yml`

## Test build stages

1. test spring boot aplication: `mvn spring-boot:run`
2. test jar: `java -jar number_generator.jar`
3. test docker image start container: `docker run -d -p 8080:8080 --rm --name number-generator-8080 number-generator`
4. kill docker container afterwards: `docker kill number_generator`
5. test kubernetes deployment

## Bsp to expose services 
kubectl expose deployment hello-node --type=NodePort --port=8080

minikube service losnummern --url






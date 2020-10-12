# ResourceGroup anlegen
`az group create --name losRG --location westeurope`

```
{
  "id": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourceGroups/losRG",
  "location": "westeurope",
  "managedBy": null,
  "name": "losRG",
  "properties": {
    "provisioningState": "Succeeded"
  },
  "tags": null
}
```

# Docker Regestry anlegen
`az acr create --resource-group losRG --name losCR --sku Basic`

```
{
  "adminUserEnabled": false,
  "creationDate": "2019-04-10T10:06:40.465400+00:00",
  "id": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourceGroups/losRG/providers/Microsoft.ContainerRegistry/registries/losCR",
  "location": "westeurope",
  "loginServer": "loscr.azurecr.io",
  "name": "losCR",
  "provisioningState": "Succeeded",
  "resourceGroup": "losRG",
  "sku": {
    "name": "Basic",
    "tier": "Basic"
  },
  "status": null,
  "storageAccount": null,
  "tags": {},
  "type": "Microsoft.ContainerRegistry/registries"
}
```

# Login in die Container Registry
`az acr login --name losCR`

```
Login Succeeded
```

# Tag das lokale Conatiner image der application um es in die azure CR zu pushen
## Name der Azure Container Registry herausfinden
`az acr list --resource-group losRG --query "[].{acrLoginServer:loginServer}" --output table`

```
AcrLoginServer
----------------
loscr.azurecr.io
```

## Tagen des lokalen Docker images
`docker tag losnummern-generator loscr.azurecr.io/losnummern-generator`

# Docker Image pushen
`docker push loscr.azurecr.io/losnummern-generator`

```
The push refers to repository [loscr.azurecr.io/losnummern-generator]
11d65a602a34: Pushed
d1cace45ead1: Pushing [===>                                               ]   52.7MB/696.8MB
a063f5436873: Pushed
c9a8a600ae18: Pushed
eec9922d69ca: Pushed
c594d901dc9d: Pushing [===============>                                   ]   43.8MB/143.3MB
b250144690ff: Pushed
d31e0e554b0a: Pushed
7034f4f565c8: Pushing [======================>                            ]  50.99MB/111.8MB
```

# Azure Container Registry Images anzeigen
`az acr repository list --name losCR --output table`

```
Result
--------------------
losnummern-generator
```

# Azure Active Directory Grant erstellen

## AD user und Passwort erfragen
`az ad sp create-for-rbac --skip-assignment`

```
{
  "appId": "7cb9dd56-19e7-48a0-ba77-eb9c388442cf",
  "displayName": "azure-cli-2019-04-10-11-17-47",
  "name": "http://azure-cli-2019-04-10-11-17-47",
  "password": "4062f237-c04c-4f7b-88bd-e6684cc52b2e",
  "tenant": "b9424c23-9233-41a4-9605-b54bffb5a90d"
}
```

## id der Azure Container Registry erfragen
`az acr show --resource-group losRG --name losCR --query "id" --output tsv`

```
/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourceGroups/losRG/providers/Microsoft.ContainerRegistry/registries/losCR
```

## Den User den Grant geben
`az role assignment create --assignee 7cb9dd56-19e7-48a0-ba77-eb9c388442cf --scope /subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourceGroups/losRG/providers/Microsoft.ContainerRegistry/registries/losCR --role acrpull`

```
{
  "canDelegate": null,
  "id": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourceGroups/losRG/providers/Microsoft.ContainerRegistry/registries/losCR/providers/Microsoft.Authorization/roleAssignments/839f459d-9f4e-475b-8fc7-026b22199823",
  "name": "839f459d-9f4e-475b-8fc7-026b22199823",
  "principalId": "8b985abc-8ae3-4816-be78-cd9a612d161e",
  "resourceGroup": "losRG",
  "roleDefinitionId": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/providers/Microsoft.Authorization/roleDefinitions/7f951dda-4ed3-4680-a7ca-43fe172d538d",
  "scope": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourceGroups/losRG/providers/Microsoft.ContainerRegistry/registries/losCR",
  "type": "Microsoft.Authorization/roleAssignments"
}
```

# Kubernetes Cluster hochfahren
`az aks create --resource-group losRG --name losAKSCluster --node-count 2 --service-principal 7cb9dd56-19e7-48a0-ba77-eb9c388442cf --client-secret 4062f237-c04c-4f7b-88bd-e6684cc52b2e --enable-addons monitoring --generate-ssh-keys`

Ein paar Minuten warten

```
{
  "aadProfile": null,
  "addonProfiles": {
    "omsagent": {
      "config": {
        "logAnalyticsWorkspaceResourceID": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourcegroups/defaultresourcegroup-weu/providers/microsoft.operationalinsights/workspaces/defaultworkspace-28cdd1b8-635e-41b3-b73a-16d547bf7812-weu"
      },
      "enabled": true
    }
  },
  "agentPoolProfiles": [
    {
      "count": 2,
      "maxPods": 110,
      "name": "nodepool1",
      "osDiskSizeGb": 100,
      "osType": "Linux",
      "storageProfile": "ManagedDisks",
      "vmSize": "Standard_DS2_v2",
      "vnetSubnetId": null
    }
  ],
  "dnsPrefix": "losAKSClus-losRG-28cdd1",
  "enableRbac": true,
  "fqdn": "losaksclus-losrg-28cdd1-01c9ed4d.hcp.westeurope.azmk8s.io",
  "id": "/subscriptions/28cdd1b8-635e-41b3-b73a-16d547bf7812/resourcegroups/losRG/providers/Microsoft.ContainerService/managedClusters/losAKSCluster",
  "kubernetesVersion": "1.11.9",
  "linuxProfile": {
    "adminUsername": "azureuser",
    "ssh": {
      "publicKeys": [
        {
          "keyData": "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCvgS8KKoH/+4gzBsNpCd0Sncj+3+GteHTSWFaGv+O7ZOvFVVCURxmhOn2LTgQZEi6krNVy+WkaAr2Mm83RAkh4A6hUCMuzNHYUoEtIpVPWYr47LXRVEH/vGr+YFn9l+hOqN9qJNPSreZckG7CVRjQgLbqbnbwXl9OEtOEJeFpxgY/Kdn6gONIBYlfILFV9xEhfjx7OxGZAT18tw8+4/9DzMjyNQLTE6/jD6+lsyf4kOcCF6Mp7jT/QYp+ue3lYJOHC/4kTsUI/MOU+p07TqFd+W0LjV/DoJWd5mi3sz/qZSxJwY0hqGYoWErntcl19eUxqHdRvfaGDG/W0t8ukQz3Hqyu3dx8NkghP0XpfrsyG95eNVPdFTACbIGCRuftFef3l0NTUJkSVJtFJfjJBWP/v9AnhQ2aOVlkoUOI7Te5fSiDy6jQNMSjfCLtjR/TQSKwzrZuG3gcbKlklPcHWqfzEZAc61P62bgEAACfEGpnKHHheRG3+fGSpVRxSWBPLR7ygq9epc4koB2xCT/4OzqS16IC7Ltk0/+c8nW8CFgXo7laHhTn14vJTuzp/0LbqwGzlkKqEw46m9+Q9d1mrNOZzGlZwMx24SE5yxlMaUI+iclAApgdH/kUr1w8qb+I4ckDcpXpeGAQc7saho8NmVoSzJd3Z47vlboBl3LysM4JAXw== Johannes.Hoehne@mt-ag.com\n"
        }
      ]
    }
  },
  "location": "westeurope",
  "name": "losAKSCluster",
  "networkProfile": {
    "dnsServiceIp": "10.0.0.10",
    "dockerBridgeCidr": "172.17.0.1/16",
    "networkPlugin": "kubenet",
    "networkPolicy": null,
    "podCidr": "10.244.0.0/16",
    "serviceCidr": "10.0.0.0/16"
  },
  "nodeResourceGroup": "MC_losRG_losAKSCluster_westeurope",
  "provisioningState": "Succeeded",
  "resourceGroup": "losRG",
  "servicePrincipalProfile": {
    "clientId": "7cb9dd56-19e7-48a0-ba77-eb9c388442cf",
    "secret": null
  },
  "tags": null,
  "type": "Microsoft.ContainerService/ManagedClusters"
}
```

# mit dem AKS Cluster verbinden

## Verbinden
`az aks get-credentials --resource-group losRG --name losAKSCluster`

## Erfolg überprüfen
`kubectl get nodes`

```
NAME                       STATUS    ROLES     AGE       VERSION
aks-nodepool1-32330896-0   Ready     agent     3m        v1.11.9
aks-nodepool1-32330896-1   Ready     agent     3m        v1.11.9
```

# losnummerngenerator installieren
## Installieren
`kubectl apply -f aks-deployment.yml`

```
deployment.apps "losnummern" created
service "losnummern-service" created
```

## installation des services prüfen
`kubectl get services --watch`

wenn alle pendings weg sind console abbrechen

```
NAME                 TYPE           CLUSTER-IP   EXTERNAL-IP     PORT(S)        AGE
kubernetes           ClusterIP      10.0.0.1     <none>          443/TCP        12m
losnummern-service   LoadBalancer   10.0.12.3    52.157.171.61   80:32036/TCP   1m
```

## Applikation überprüfen
Es dauert einige Zeit, bis hinter der IP wirklich der service läuft. Geduld.

`curl 52.157.171.61`

```
{
   "myid" : "d90de534-fdb8-4f56-8f05-b1a7a5ab2076",
   "randomNumber" : 588123,
   "hostName" : "losnummern-75b8b75bd8-b2sbr"
}
```

# Resource Group wieder löschen
`az group delete --name losRG --yes --no-wait`

Dauert einige Minute

## Run it locally

- Install and start minikube.
- Make sure your local cluster can access the docker registry. [Pull an Image from a Private Registry](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/).
- Replace image path in `k8s.yml` with your image path.
- Replace docker workspace in `run-locally.sh` and execute it with the proper tag: `run-locally.sh <tag>`.
- Enable port-forwarding either by leveraging `kubectl` or a tool of your choice, e.g. `k9s`.
- Use the provided requests located under folder [postman](postman/micronaut-kubernetes-testing.postman_collection.json) to test around.

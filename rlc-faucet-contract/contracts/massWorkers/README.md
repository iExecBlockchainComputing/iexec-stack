The script deploy_worker.sh starts n worker on the machine. A few variables should be modified in the .env file to configure the deployment:
⋅⋅* WORKER_DOCKER_IMAGE_VERSION
⋅⋅* SCHEDULER_DOMAIN
⋅⋅* SCHEDULER_IP
⋅⋅* WORKER_LOGIN
⋅⋅* WORKER_PASSWORD

The number of workers deployed is configured in the script itself.

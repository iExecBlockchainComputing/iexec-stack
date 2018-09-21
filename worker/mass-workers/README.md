# Create hosts file
```bash
# HOSTNAME HOST USER SSH_KEY SSH_PORT WORKER_TAG
worker1 worker1.iex.ec ubuntu ~/.ssh/key-pem.pem 22 none
```

# Create variables file
```bash
# Setting variables
SCHEDULER_DOMAIN=xxxxx
SCHEDULER_IP=xxxxx

WORKER=worker-net

WORKER_DOCKER_IMAGE_VERSION=xxxxx
WORKER_HOSTNAME=hostname-worker-net-docker
WORKER_LOGIN=vworker
WORKER_PASSWORD=xxxxx
WORKER_LOGGERLEVEL=FINEST
WORKER_SHAREDPACKAGES=
WORKER_SHAREDAPPS=docker
WORKER_TMPDIR=/tmp/worker-net
WORKER_SANDBOX_ENABLED=true
```

# Script usage
```bash
./deploy.sh env_file [--all-from-file|--line-from-file] file_name line_number [--stop-only]
```

# To deploy workers on all hosts from file
```bash
./deploy.sh variables-dev.env --all-from-file hosts-dev  [--stop-only]
```

# To deploy worker on specific host from file
```bash
 ./deploy.sh variables-dev.env --line-from-file hosts-dev 1  [--stop-only]
```


# Check if variables where set
if [ -z "${SCHEDULER_DOMAIN}" ]; then
  echo "Variables not set."
  exit 1
fi

# Function which checks exit status and stops execution
function checkExitStatus() {
  if [ $1 -eq 0 ]; then
    echo OK
  else
    echo $2
    exit 1
  fi
}

# Ensure that script is launched as root
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

# CloudInit iexec worker script
echo "iExec worker deployment started!"

echo "Updating and Generating locale..."
export DEBIAN_FRONTEND=noninteractive
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US.UTF-8
update-locale LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 LANGUAGE=en_US.UTF-8
locale-gen

# Updating worker VM
apt-get update
apt-get -y upgrade
apt-get -y dist-upgrade
apt-get -y autoremove

# If docker-ce or docker-engine is not already installed installing it
if [ $(dpkg-query -W -f='${Status}' docker-ce 2>/dev/null | grep -c "ok installed") -eq 0 ] && [ $(dpkg-query -W -f='${Status}' docker-engine 2>/dev/null | grep -c "ok installed") -eq 0 ] ; then
    echo "Installing docker..."
    apt-get update
    apt-get install -y apt-transport-https ca-certificates curl software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    apt-get update
    apt-get install -y docker-ce
    usermod -aG docker ubuntu
    docker --version
    #checkExitStatus $? "Failed to install docker..."
fi

# If docker compose is not already installed installing it
if [ ! -f /usr/local/bin/docker-compose ]; then
    echo "Installing docker-compose..."
    curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    docker-compose --version
    #checkExitStatus $? "Failed to install docker-compose..."
fi


# Stopping and deleting old version of worker
echo "Stopping and deleting old version of worker..."
docker-compose -f /opt/iexec-${WORKER}/docker-compose.yml down

# Creating iexec worker docker-compose file
mkdir -p /opt/iexec-${WORKER}
mkdir -p /opt/iexec-${WORKER}/wallet

echo "Creating docker-compose file..."
cat >/opt/iexec-${WORKER}/docker-compose.yml << EOF
version: '3.4'
services:
  worker:
    image: iexechub/worker:${WORKER_DOCKER_IMAGE_VERSION}
    hostname: ${WORKER_HOSTNAME}
    restart: always
    environment:
      - SCHEDULER_DOMAIN=${SCHEDULER_DOMAIN}
      - SCHEDULER_IP=${SCHEDULER_IP}
      - LOGIN=${WORKER_LOGIN}
      - PASSWORD=${WORKER_PASSWORD}
      - LOGGERLEVEL=${WORKER_LOGGERLEVEL}
      - SHAREDPACKAGES=${WORKER_SHAREDPACKAGES}
      - SHAREDAPPS=${WORKER_SHAREDAPPS}
      - TMPDIR=${WORKER_TMPDIR}
      - SANDBOXENABLED=${WORKER_SANDBOX_ENABLED}
      - WALLETPASSWORD=${WORKERWALLETPASSWORD}
      - BLOCKCHAINETHENABLED=${BLOCKCHAINETHENABLED}
    volumes:
      - ${WORKERWALLETPATH}:/iexec/wallet/wallet_worker.json
      - /var/run/docker.sock:/var/run/docker.sock
      - ${WORKER_TMPDIR}:${WORKER_TMPDIR}
EOF

echo "Docker compose up..."
docker-compose -f /opt/iexec-${WORKER}/docker-compose.yml up -d --force-recreate

echo "iExec worker deployment finished!"

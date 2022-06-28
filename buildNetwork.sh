export PATH=${PWD}/bin:$PATH
export FABRIC_CFG_PATH=${PWD}/config

. utils.sh
. createOrganizations.sh

function deleteDocker() {
    infoln "remove build folder"
    sudo rm -rf .build
    infoln "stop docker containers"
    docker stop $(docker ps -a -q)
    infoln "remove docker containers"
    docker rm $(docker ps -a -q)
}

function createOrgs() {
    infoln "Generating certificates using Fabric CA"

    IMAGE_TAG=${CA_IMAGETAG} docker-compose -f $COMPOSE_FILE_CA up -d 2>&1

    . createOrganizations.sh


  while :
    do
      if [ ! -f ".build/organizations/fabric-ca/ca-educationOrg1/tls-cert.pem" ]; then
        sleep 1
      else
        break
      fi
    done

    infoln "Create education Organiation Identities"

    sudo chown -R thuthi:thuthi .build
    sudo chmod -R 755 .build

    createEducationOrg

    # infoln "Creating Org2 Identities"

    # createOrg2

    # infoln "Creating Orderer Org Identities"

    # createOrderer

  infoln "Generating CCP files for Org1 and Org2"
  ./organizations/ccp-generate.sh
}

function createConsortium() {
  infoln "Generating Orderer Genesis block"

  set -x
  configtxgen -profile TwoOrgsOrdererGenesis -channelID system-channel -outputBlock ./system-genesis-block/genesis.block
  res=$?
  { set +x; } 2>/dev/null
  if [ $res -ne 0 ]; then
    fatalln "Failed to generate orderer genesis block..."
  fi
}

# After we create the org crypto material and the system channel genesis block,
# we can now bring up the peers and ordering service. By default, the base
# file for creating the network is "docker-compose-test-net.yaml" in the ``docker``
# folder. This file defines the environment variables and file mounts that
# point the crypto material and genesis block that were created in earlier.

# Bring up the peer and orderer nodes using docker compose.
function networkUp() {
    createOrgs
    createConsortium

  COMPOSE_FILES="-f ${COMPOSE_FILE_BASE}"

  if [ "${DATABASE}" == "couchdb" ]; then
    COMPOSE_FILES="${COMPOSE_FILES} -f ${COMPOSE_FILE_COUCH}"
  fi

  IMAGE_TAG=$IMAGETAG docker-compose ${COMPOSE_FILES} up -d 2>&1

  docker ps -a
  if [ $? -ne 0 ]; then
    fatalln "Unable to start network"
  fi
}

# Obtain the OS and Architecture string that will be used to select the correct
# native binaries for your platform, e.g., darwin-amd64 or linux-amd64
OS_ARCH=$(echo "$(uname -s | tr '[:upper:]' '[:lower:]' | sed 's/mingw64_nt.*/windows/')-$(uname -m | sed 's/x86_64/amd64/g')" | awk '{print tolower($0)}')
# Using crpto vs CA. default is cryptogen
CRYPTO="cryptogen"
# timeout duration - the duration the CLI should wait for a response from
# another container before giving up
MAX_RETRY=5
# default for delay between commands
CLI_DELAY=3
# channel name defaults to "mychannel"
CHANNEL_NAME="mychannel"
# chaincode name defaults to "NA"
CC_NAME="NA"
# chaincode path defaults to "NA"
CC_SRC_PATH="NA"
# endorsement policy defaults to "NA". This would allow chaincodes to use the majority default policy.
CC_END_POLICY="NA"
# collection configuration defaults to "NA"
CC_COLL_CONFIG="NA"
# chaincode init function defaults to "NA"
CC_INIT_FCN="NA"
# use this as the default docker-compose yaml definition
COMPOSE_FILE_BASE=docker/docker-compose-test-net.yaml
# docker-compose.yaml file if you are using couchdb
COMPOSE_FILE_COUCH=docker/docker-compose-couch.yaml
# use this as the docker compose couch file for org3
COMPOSE_FILE_COUCH_ORG3=addOrg3/docker/docker-compose-couch-org3.yaml
# use this as the default docker-compose yaml definition for org3
COMPOSE_FILE_ORG3=addOrg3/docker/docker-compose-org3.yaml
#
# chaincode language defaults to "NA"
CC_SRC_LANGUAGE="NA"
# Chaincode version
CC_VERSION="1.0"
# Chaincode definition sequence
CC_SEQUENCE=1
# default image tag
IMAGETAG="latest"
# default ca image tag
CA_IMAGETAG="latest"
# default database
DATABASE="leveldb"

# certificate authorities compose file
COMPOSE_FILE_CA=config/docker/docker-compose-ca.yaml

if [ $# != 1 ]; then
    errorln "스크립트 매개변수가 1개 이어야 합니다."
    exit 0
fi

if [ $1 == "down" ]; then
    deleteDocker
elif [ $1 == "up" ]; then
    createOrgs
elif [ $1 == "test" ]; then
    echo 'NodeOUs:
  Enable: true
  ClientOUIdentifier:
    Certificate: cacerts/localhost-6055-ca-educationOrg1.pem
    OrganizationalUnitIdentifier: client
  PeerOUIdentifier:
    Certificate: cacerts/localhost-6055-ca-educationOrg1.pem
    OrganizationalUnitIdentifier: peer
  AdminOUIdentifier:
    Certificate: cacerts/localhost-6055-ca-educationOrg1.pem
    OrganizationalUnitIdentifier: admin
  OrdererOUIdentifier:
    Certificate: cacerts/localhost-6055-ca-educationOrg1.pem
    OrganizationalUnitIdentifier: orderer' > ${PWD}/.build/organizations/educationOrganizations/educationOrg1.jo2seo.com/msp/config.yaml
fi

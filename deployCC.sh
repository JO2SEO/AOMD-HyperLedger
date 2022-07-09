#! /bin/bash

. config/scripts/utils.sh

function deployCCPeer() {
    CHANNEL_NAME=$1
    ORG_GROUP_NAME=$2
    ORG_NAME=$3
    MSP_NAME=$4
    CC_NAME=$5
    CC_VERSION=$6
    PEER_NAME=$7
    PEER_PORT=$8
    infoln "CC 패키지를 ${ORG_NAME}의 ${PEER_NAME}에게 배포합니다."

    export CORE_PEER_TLS_ENABLED=true
    export CORE_PEER_LOCALMSPID=$MSP_NAME
    export CORE_PEER_TLS_ROOTCERT_FILE=$PWD/.build/organizations/$ORG_GROUP_NAME/$ORG_NAME.aomd.com/peers/$PEER_NAME.$ORG_NAME.aomd.com/tls/ca.crt
    export CORE_PEER_MSPCONFIGPATH=$PWD/.build/organizations/$ORG_GROUP_NAME/$ORG_NAME.aomd.com/users/Admin@$ORG_NAME.aomd.com/msp
    export CORE_PEER_ADDRESS=localhost:$PEER_PORT

    peer lifecycle chaincode install $CC_NAME.tar.gz

    # 정상설치 되었는지 체크
    infoln "정상 설치되어 있는지 확인합니다"
    peer lifecycle chaincode queryinstalled >&log.txt
    PACKAGE_ID=$(sed -n "/${CC_NAME}_${CC_VERSION}/{s/^Package ID: //; s/, Label:.*$//; p;}" log.txt)
    echo $PACKAGE_ID

    # CC를 Org에게 배포합니다.
    peer lifecycle chaincode approveformyorg -o localhost:9050 --ordererTLSHostnameOverride orderer.aomd.com --channelID $CHANNEL_NAME --name $CC_NAME --version $CC_VERSION --package-id $PACKAGE_ID --sequence 1 --tls --cafile "${PWD}/.build/organizations/ordererOrganizations/orderer.aomd.com/orderers/orderer.aomd.com/msp/tlscacerts/tlsca.aomd.com-cert.pem"
}

function deployCCOrgnization() {
    CHANNEL_NAME=$1
    ORG_GROUP_NAME=$2
    ORG_NAME=$3
    MSP_NAME=$4
    CC_NAME=$5
    CC_VERSION=$6
    # CC 배포를 위해 패키지화
    infoln "CC 배포를 위해 패키지화 합니다."
    export PATH=$PWD/bin:$PATH
    export FABRIC_CFG_PATH=$PWD/config
    export CORE_PEER_MSPCONFIGPATH=$PWD/.build/organizations/$ORG_GROUP_NAME/$ORG_NAME.aomd.com/users/Admin@$ORG_NAME.aomd.com/msp

    peer lifecycle chaincode package $CC_NAME.tar.gz --path chaincode-java/build/install --lang java --label ${CC_NAME}_${CC_VERSION}

    # 패키지를 peer에 배포
    deployCCPeer $1 $2 $3 $4 $5 $6 peer0 6051
}

deployCCOrgnization educationchannel educationOrganizations educationOrg1 EducationOrg1MSP educationCC 1.0
#!/bin/bash

function createChannelTx() {
    PROFILE_NAME=$1
    CHANNEL_NAME=$2
    
    infoln "Generating channel create transaction '${CHANNEL_NAME}.tx'"

    set -x
	configtxgen -profile $PROFILE_NAME -outputCreateChannelTx .build/channel-artifacts/${CHANNEL_NAME}.tx -channelID $CHANNEL_NAME
	res=$?
	{ set +x; } 2>/dev/null
}

function createChannel() {
    MSP_NAME=$1
    ORG_GROUP_NAME=$2
    ORG_NAME=$3
    PEER_PORT=$4
    CHANNEL_NAME=$5
    ORDERER_PORT=9050 
    export CORE_PEER_TLS_ENABLED=true
    export ORDERER_CA=.build/organizations/ordererOrganizations/orderer.aomd.com/orderers/orderer.aomd.com/msp/tlscacerts/tlsca.aomd.com-cert.pem
    export CORE_PEER_LOCALMSPID=$MSP_NAME
    export CORE_PEER_TLS_ROOTCERT_FILE=.build/organizations/$ORG_GROUP_NAME/$ORG_NAME.aomd.com/peers/peer0.$ORG_NAME.aomd.com/tls/ca.crt
    export CORE_PEER_MSPCONFIGPATH=.build/organizations/$ORG_GROUP_NAME/$ORG_NAME.aomd.com/users/Admin@$ORG_NAME.aomd.com/msp
    export CORE_PEER_ADDRESS=localhost:$PEER_PORT

    infoln "$CHANNEL_NAME를 만드는 중입니다."

    local rc=1
    local COUNTER=1
    while [ $rc -ne 0 -a $COUNTER -lt 10 ] ; do
        sleep 3
        set -x

        peer channel create -o localhost:$ORDERER_PORT -c $CHANNEL_NAME --ordererTLSHostnameOverride orderer.AOMD.com -f .build/channel-artifacts/${CHANNEL_NAME}.tx --outputBlock .build/channel-artifacts/${CHANNEL_NAME}.block --tls --cafile $ORDERER_CA >&log.txt

        res=$?
		{ set +x; } 2>/dev/null
        let rc=$res
		COUNTER=$(expr $COUNTER + 1)
    done
    cat log.txt

    joinChannel $CHANNEL_NAME $ORG_NAME
    setAnchorPeer $ORG_NAME $CHANNEL_NAME

    successln "채널 $CHANNEL_NAME을 생성하고, Organization들이 참여했습니다."

    docker exec cli scripts/setAnchorPeer.sh $MSP_NAME $ORG_GROUP_NAME $ORG_NAME $PEER_PORT $CHANNEL_NAME 
}

function joinChannel() {
    CHANNEL_NAME=$1
    ORG_NAME=$2

    FABRIC_CFG_PATH=$PWD
    BLOCKFILE=$PWD/.build/channel-artifacts/${CHANNEL_NAME}.block

    echo $BLOCKFILE

    local rc=1
	local COUNTER=1
	## Sometimes Join takes time, hence retry
	while [ $rc -ne 0 -a $COUNTER -lt 5 ] ; do
    sleep 3
    set -x
    peer channel join -b $BLOCKFILE >&log.txt
    res=$?
    { set +x; } 2>/dev/null
		let rc=$res
		COUNTER=$(expr $COUNTER + 1)
	done
    cat log.txt
}

function createChannels() {
    export FABRIC_CFG_PATH=${PWD}/config

    createChannelTx EducationChannel educationchannel
    createChannelTx AwardChannel awardchannel
    createChannelTx LicenseChannel licensechannel

    export FABRIC_CFG_PATH=${PWD}
    cp config/core.yaml ./core.yaml

    createChannel EducationOrg1MSP educationOrganizations educationOrg1 6051 educationchannel

    createChannel AwardOrg1MSP awardOrganizations awardOrg1 7051 awardchannel

    createChannel LicenseOrg1MSP licenseOrganizations licenseOrg1 8051 licensechannel
}
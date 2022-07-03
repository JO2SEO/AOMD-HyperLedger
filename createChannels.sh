

function createChannelTx() {
    PROFILE_NAME=$1
    CHANNEL_NAME=$2
    
    infoln "Generating channel create transaction '${CHANNEL_NAME}.tx'"

    set -x
	configtxgen -profile $PROFILE_NAME -outputCreateChannelTx .build/channel-artifacts/${CHANNEL_NAME}.tx -channelID $CHANNEL_NAME
	res=$?
	{ set +x; } 2>/dev/null
}


function createChannels() {
    $FABRIC_CFG_PATH

    createChannelTx EducationChannel educationchannel
    createChannelTx AwardChannel awardchannel
    createChannelTx LicenseChannel licensechannel
}

export FABRIC_CFG_PATH=${PWD}/config
createChannels
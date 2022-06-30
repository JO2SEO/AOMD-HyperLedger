#!/bin/bash
function createOrganization() {
    #Varaibles
    CA_NAME=$1
    ORG_GROUP_NAME=$2
    ORG_NAME=$3
    PORT=$4
    BUILD_FOLDER=${PWD}/.build/organizations
    FABRIC_CA_CLIENT_HOME=${BUILD_FOLDER}/${ORG_GROUP_NAME}/${ORG_NAME}
    CERT_FILE=${BUILD_FOLDER}/fabric-ca/${CA_NAME}/tls-cert.pem

    mkdir -p ${FABRIC_CA_CLIENT_HOME}/msp

    infoln "Enrolling the CA admin"

    set -x
    fabric-ca-client enroll -u https://admin:adminpw@localhost:$PORT --caname ${CA_NAME} --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    echo "NodeOUs:
        Enable: true
        ClientOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: client
        PeerOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: peer
        AdminOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: admin
        OrdererOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: orderer" > ${FABRIC_CA_CLIENT_HOME}/msp/config.yaml

    #Peer0 
    PEER0=peer0.${ORG_NAME}
    infoln "Register peer0 on $ORG_NAME"
    set -x
    fabric-ca-client register -u https://localhost:$PORT --caname ${CA_NAME} --id.name peer0 --id.secret peer0pw --id.type peer --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    infoln "Registering user"
    set -x
    fabric-ca-client register -u https://localhost:$PORT --caname ${CA_NAME} --id.name user1 --id.secret user1pw --id.type client --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    infoln "Registering the org admin"
    set -x
    fabric-ca-client register -u https://localhost:$PORT --caname ${CA_NAME} --id.name org1admin --id.secret org1adminpw --id.type admin --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    infoln "Generating the peer0 msp"
    set -x
    fabric-ca-client enroll -u https://peer0:peer0pw@localhost:$PORT --caname ${CA_NAME} -M ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/msp --csr.hosts ${PEER0} --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    cp ${FABRIC_CA_CLIENT_HOME}/msp/config.yaml ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/msp/config.yaml

    infoln "Generating the peer0-tls certificates"
    set -x
    fabric-ca-client enroll -u https://peer0:peer0pw@localhost:$PORT --caname ${CA_NAME} -M ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls --enrollment.profile tls --csr.hosts ${PEER0} --csr.hosts localhost --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    cp ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/tlscacerts/* ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/ca.crt
    cp ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/signcerts/* ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/server.crt
    cp ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/keystore/* ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/server.key

    mkdir -p ${FABRIC_CA_CLIENT_HOME}/msp/tlscacerts
    cp ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/tlscacerts/* ${FABRIC_CA_CLIENT_HOME}/msp/tlscacerts/ca.crt

    mkdir -p ${FABRIC_CA_CLIENT_HOME}/tlsca
    cp ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/tls/tlscacerts/* ${FABRIC_CA_CLIENT_HOME}/tlsca/tlsca.${ORG_NAME}-cert.pem

    mkdir -p ${FABRIC_CA_CLIENT_HOME}/ca
    cp ${FABRIC_CA_CLIENT_HOME}/peers/${PEER0}/msp/cacerts/* ${FABRIC_CA_CLIENT_HOME}/ca/ca.${ORG_NAME}-cert.pem

    infoln "Generating the user msp"
    set -x
    fabric-ca-client enroll -u https://user1:user1pw@localhost:$PORT --caname ${CA_NAME} -M ${FABRIC_CA_CLIENT_HOME}/users/User1@${ORG_NAME}/msp --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    cp ${FABRIC_CA_CLIENT_HOME}/msp/config.yaml ${FABRIC_CA_CLIENT_HOME}/users/User1@${ORG_NAME}/msp/config.yaml

    infoln "Generating the org admin msp"
    set -x
    fabric-ca-client enroll -u https://org1admin:org1adminpw@localhost:$PORT --caname ${CA_NAME} -M ${FABRIC_CA_CLIENT_HOME}/users/Admin@${ORG_NAME}/msp --tls.certfiles ${CERT_FILE}
    { set +x; } 2>/dev/null

    cp ${FABRIC_CA_CLIENT_HOME}/msp/config.yaml ${FABRIC_CA_CLIENT_HOME}/users/Admin@${ORG_NAME}/msp/config.yaml
}

function createOrderer() {
    CA_NAME=$1
    ORG_GROUP_NAME=$2
    ORG_NAME=$3
    PORT=$4
    BUILD_FOLDER=${PWD}/.build/organizations
    FABRIC_CA_CLIENT_HOME=${BUILD_FOLDER}/${ORG_GROUP_NAME}/${ORG_NAME}
    CERT_FILE=${BUILD_FOLDER}/fabric-ca/${CA_NAME}/tls-cert.pem
    HOST=orderer.jo2seo.com

    mkdir -p $FABRIC_CA_CLIENT_HOME/msp
    #mkdir -p organizations/ordererOrganizations/example.com

    infoln "Enrolling the CA admin"

    #export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/ordererOrganizations/example.com

    set -x
    fabric-ca-client enroll -u https://admin:adminpw@localhost:$PORT --caname $CA_NAME --tls.certfiles $CERT_FILE
    { set +x; } 2>/dev/null

    echo "NodeOUs:
        Enable: true
        ClientOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: client
        PeerOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: peer
        AdminOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: admin
        OrdererOUIdentifier:
            Certificate: cacerts/localhost-$PORT-$CA_NAME.pem
            OrganizationalUnitIdentifier: orderer" > $FABRIC_CA_CLIENT_HOME/msp/config.yaml

    infoln "Registering orderer"
    set -x
    fabric-ca-client register -u https://localhost:$PORT --caname $CA_NAME --id.name orderer --id.secret ordererpw --id.type orderer --tls.certfiles $CERT_FILE
    { set +x; } 2>/dev/null

    infoln "Registering the orderer admin"
    set -x
    fabric-ca-client register -u https://localhost:$PORT --caname $CA_NAME --id.name ordererAdmin --id.secret ordererAdminpw --id.type admin --tls.certfiles $CERT_FILE
    { set +x; } 2>/dev/null

    infoln "Generating the orderer msp"
    set -x
    fabric-ca-client enroll -u https://orderer:ordererpw@localhost:$PORT --caname $CA_NAME -M $FABRIC_CA_CLIENT_HOME/orderers/$HOST/msp --csr.hosts $HOST --csr.hosts localhost --tls.certfiles $CERT_FILE
    { set +x; } 2>/dev/null

    cp $FABRIC_CA_CLIENT_HOME/msp/config.yaml $FABRIC_CA_CLIENT_HOME/orderers/$HOST/msp/config.yaml

    infoln "Generating the orderer-tls certificates"
    set -x
    fabric-ca-client enroll -u https://orderer:ordererpw@localhost:$PORT --caname $CA_NAME -M $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls --enrollment.profile tls --csr.hosts $HOST --csr.hosts localhost --tls.certfiles $CERT_FILE
    { set +x; } 2>/dev/null

    cp $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/tlscacerts/* $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/ca.crt
    cp $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/signcerts/* $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/server.crt
    cp $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/keystore/* $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/server.key

    mkdir -p $FABRIC_CA_CLIENT_HOME/orderers/$HOST/msp/tlscacerts
    cp $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/tlscacerts/* $FABRIC_CA_CLIENT_HOME/orderers/$HOST/msp/tlscacerts/tlsca.jo2seo.com-cert.pem

    mkdir -p $FABRIC_CA_CLIENT_HOME/msp/tlscacerts
    cp $FABRIC_CA_CLIENT_HOME/orderers/$HOST/tls/tlscacerts/* $FABRIC_CA_CLIENT_HOME/msp/tlscacerts/tlsca.jo2seo.com-cert.pem

    infoln "Generating the admin msp"
    set -x
    fabric-ca-client enroll -u https://ordererAdmin:ordererAdminpw@localhost:$PORT --caname $CA_NAME -M $FABRIC_CA_CLIENT_HOME/users/Admin@$ORG_NAME/msp --tls.certfiles $CERT_FILE
    { set +x; } 2>/dev/null

    cp $FABRIC_CA_CLIENT_HOME/msp/config.yaml $FABRIC_CA_CLIENT_HOME/users/Admin@$ORG_NAME/msp/config.yaml
}

function gen_cpp() {
    ORG_GROUP_NAME=$1
    ORG=$2
    P0PORT=$3
    CAPORT=$4
    PEERPEM=./build/organizations/$ORG_GROUP_NAME/$ORG.jo2seo.com/tlsca/tlsca.$ORG.jo2seo.com-cert.pem
    CAPEM=./build/organizations/$ORG_GROUP_NAME/$ORG.jo2seo.com/ca/ca.$ORG.jo2seo.com-cert.pem


}

function createOrganizations() {
    createOrganization ca-educationOrg1 educationOrganizations educationOrg1.jo2seo.com  6055
    createOrganization ca-awardOrg1 awardOrganizations awardOrg1.jo2seo.com 7055
    createOrganization ca-licenseOrg1 licenseOrganizations licenseOrg1.jo2seo.com 8055
    createOrderer ca-ordererOrg ordererOrganizations ordererOrg.jo2seo.com 9055 

    gen_ccp
}
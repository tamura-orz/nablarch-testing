#!/bin/bash
set -euo pipefail


### Import nablarch env.
#CUR=$(cd $(dirname $0); pwd)
source $HOME/build-script/travis-ci/nablarch_env

cd ${TRAVIS_BUILD_DIR}

./gradlew clean test -PnablarchRepoUsername=${NABLARCH_USER} -PnablarchRepoPassword=aaa \
                           -PnablarchRepoReferenceUrl=${DEVELOP_REPO_URL} -PnablarchRepoReferenceName=${DEVELOP_REPO_NAME} \
                           -PnablarchRepoDeployUrl=dav:${DEVELOP_REPO_URL} -PnablarchRepoName=${DEVELOP_REPO_NAME} \
                           -PdevelopLibUrl=${DEVELOP_REPO_URL}/${DEVELOP_REPO_NAME}

#!/bin/bash
set -euo pipefail


### Import nablarch env.
#CUR=$(cd $(dirname $0); pwd)
source $HOME/build-script/travis-ci/nablarch_env

cd ${TRAVIS_BUILD_DIR}


### Main Build.
# if it creates pull request, execute `gradlew build` only.
# if it merges pull request to develop branch or dilectly commit on develop branch, execute `gradlew uploadArchives`.
# Waning, TRAVIS_PULL_REQUEST variable is 'false' or pull request number, 1,2,3 and so on.
if [ "${TRAVIS_PULL_REQUEST}" == "false" -a "${TRAVIS_BRANCH}" == "develop"  ]; then
  echo "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ aaaa @@@@@@@@@@@@@@@@@@@@@@@"
  hostname

  ./gradlew clean test -PnablarchRepoUsername=${NABLARCH_USER} -PnablarchRepoPassword=aaa \
                           -PnablarchRepoReferenceUrl=${DEVELOP_REPO_URL} -PnablarchRepoReferenceName=${DEVELOP_REPO_NAME} \
                           -PnablarchRepoDeployUrl=dav:${DEVELOP_REPO_URL} -PnablarchRepoName=${DEVELOP_REPO_NAME} \
                           -PdevelopLibUrl=${DEVELOP_REPO_URL}/${DEVELOP_REPO_NAME}
else
  ./gradlew clean test -PnablarchRepoUsername=${NABLARCH_USER} -PnablarchRepoPassword=aaa \
                  -PnablarchRepoReferenceUrl=${DEVELOP_REPO_URL} -PnablarchRepoReferenceName=${DEVELOP_REPO_NAME} \
                  -PnablarchRepoDeployUrl=dav:${DEVELOP_REPO_URL} -PnablarchRepoName=${DEVELOP_REPO_NAME} \
                  -PdevelopLibUrl=${DEVELOP_REPO_URL}/${DEVELOP_REPO_NAME}
fi


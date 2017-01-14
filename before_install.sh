#!/bin/bash
#
# Copyright 2016-2017 Sharmarke Aden.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
set -e

if [ "$TRAVIS_PULL_REQUEST" = "false" ]; then
    echo "Decrypting Secrets"
    openssl aes-256-cbc -K $encrypted_8da71b22b126_key -iv $encrypted_8da71b22b126_iv -in secrets.tar.gz.enc -out secrets.tar.gz -d
    tar -xvzf secrets.tar.gz
fi

echo "MAVEN_OPTS='-client -Xms512m -Xmx2048m'" > ~/.mavenrc

echo "Before Install Operations All Done!"

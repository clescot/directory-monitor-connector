#!/usr/bin/env bash
echo 'starting zookeeper,kafka and kakfa-connect'
DOCKER_HOST_ADDRESS=$(ip route get 8.8.8.8 | awk 'NR==1 {print $NF}')
echo "my_ip=$DOCKER_HOST_ADDRESS";
export DOCKER_HOST_ADDRESS;
docker stack deploy -c docker-compose.yml  test
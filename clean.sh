#!/usr/bin/env bash
docker rm -f $(docker ps -a -q)
#sudo docker rmi $(sudo docker images -a -q)
docker rmi $(docker images | grep "^<none>" | awk "{print $3}")

docker images

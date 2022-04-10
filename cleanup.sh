#!/bin/bash
set -e
docker kill $(docker ps -q)
docker system prune -af
docker volume rm $(docker volume ls -q)
sudo rm -rf ${PWD}/datadir/*
sudo rm -rf ${PWD}/data/*
sudo rm -rf ${PWD}/events/*
sudo rm -rf ${PWD}/config/*



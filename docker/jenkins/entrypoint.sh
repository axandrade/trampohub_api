#!/bin/bash
set -e

if [ -S /var/run/docker.sock ]; then
  chmod 666 /var/run/docker.sock || true
fi

exec gosu jenkins /usr/bin/tini -- /usr/local/bin/jenkins.sh "$@"

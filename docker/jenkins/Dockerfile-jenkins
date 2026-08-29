FROM jenkins/jenkins:lts

USER root
RUN apt-get update && apt-get install -y \
    maven \
    git \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*

USER jenkins
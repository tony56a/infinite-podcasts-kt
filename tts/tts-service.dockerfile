FROM ghcr.io/matatonic/openedai-speech

RUN piper --update-voices --data-dir voices --download-dir voices --model en_US-arctic-medium
RUN piper --data-dir voices --download-dir voices --model en_US-arctic-medium
RUN piper --update-voices --data-dir voices --download-dir voices --model en_GB-vctk-medium
RUN piper --data-dir voices --download-dir voices --model en_GB-vctk-medium

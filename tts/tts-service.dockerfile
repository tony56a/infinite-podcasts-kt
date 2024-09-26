FROM mycroftai/mimic3

COPY tts/const.py /home/mimic3/app/mimic3_tts/

USER root

RUN /home/mimic3/app/.venv/bin/python3 -m mimic3_tts.download 'en_US/cmu-arctic_low'
RUN /home/mimic3/app/.venv/bin/python3 -m mimic3_tts.download 'en_US/vctk_low'

user mimic3

ENTRYPOINT ["/home/mimic3/app/.venv/bin/python3", "-m", "mimic3_http", "--num-threads", "8"]
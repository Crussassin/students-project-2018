FROM python:3.7.0b5

COPY /greetings_app/* greetings_app/

RUN pip install -r /greetings_app/requirements.txt

ENV DB_URL sqlite:///foo.db



entrypoint ["python","greetings_app/app.py"]

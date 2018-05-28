FROM python:3.7.0b4

RUN apt-get update \
       && apt-get -y upgrade \
       && apt-get install -y net-tools iputils-ping \
       && apt-get install python net-tools -y

COPY /greetings_app/* greetings_app/

RUN pip install -r /greetings_app/requirements.txt

ENV DB_URL sqlite:///foo.db



ENTRYPOINT ["python","greetings_app/app.py"]

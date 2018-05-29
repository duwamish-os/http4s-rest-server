http4s REST server
------------------

```
java -jar target/scala-2.12/http4s-hello-server.jar
```

```bash
$ curl --request POST -d '{"correlationId": "1", "utterance": "coffee near me"}' localhost:8080/api/chat
{"correlationId":"1","displayText":"hi, how can i help you?"}

```

```
curl localhost:8080/api/chat/prayagupd
{"body":"welcome to sellpeace.com, Mr. prayagupd"}
```

![](perf.png)
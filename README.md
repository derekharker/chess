# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

Project Diagram:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUe4cOpO+51d42X9oQOuFIiO73DCTyltiaJ4movZYKBcLtn6pavIaSpLPU+wgheep-gBaHLJcCaUO2KYYPU4ROE4WYTChnwwOhwLLFh8Q4fWdH4Q26AcKYXi+AE0DsIyMQinAEbSHACgwAAMhAWSFCRzBOtQ-rNG0XS9AY6j5GgWaKnMay-P8HBXIhgpwf6Iy6Sg+n6H8OzQo88GKVQSIwAgMnihi0myQSRJgKSQGGLuNL7vUNRIAAZpYU6WdZWw7HOwV3kuwrkBGkkRnA7Rlu8SqxbZAIwOF3gzNlN6Je+TkuTFMAGTs26BVSt78vUh5yCgz7xNO5ZzAlvJJZUy4wAAkgAcmQEZpFlTEsWgw0je0LQ-nqZV9RVn6dvU03-ug9XtmZpZeeKGSqDBmD7Qh1TmdVW0AcZl3wsmyCpjA5GUaM12-ttaBXJx3HeH4-heCg6AxHEiRAyDXm+Fg8mCoh9QNNIaURu0EbdD0GmqFpww3TtJkgQ55m499Z2E-C63OS6rkydDnk06ePlqH5u1Bat9IwGFkUdeen0Ab1C4Csl9QimkLQALJLfqADiKOSzNK0CxdG1y19LONeV7McCg3DHpePPYV9-P7veKUi+LksyMjqMqwBCvGx+HaU9+PZ9izBOwv6UMM9BCCwWTSs1N2vb9vjj0lGAZEUY2XGeP9AQouu-jYOKGqSWiMBS0qGiw05yky2pPT2EqOO83j92mWTLzE-ZHvk47LnIDkmc5hijdgM3aiMySasyE17Oc5Y3PE0b-VCvURUQCVTEZ7LxN20llVU3PAWCm+7OMmAEY1JYHcYiP74DSlE8lUXcwz1lp8oEN0hrJs8S6igbqcmsySgGqT+5TAl8jUq89rfXVM24tHCh3a+qhb730nDRNQL8EBvw-jmNY38s5u0qPteobcO6qGOqdc6Dt4YwHGJfVQCwGhEKVNfPCYxvh3wfgg+YtQQSvxAO-aBCwQTIM+LUC4MBOh3SUg9So8lI5OBgFRYhpDyFzEoWxahIJaFQO6gwphcCWH0PYcsThDCeF8Ojn9Xi-gOAAHY3BOBQKI-wEZghwBEgANngBOQwHcYBFCemUCmylWgdHRpfEuBsAJZi0fwxMaDK6EOJkgpUP85g1zAnDL8LVHG71auiDuXdmYr1ZgLRJR4UC7wiV-KJn9L7X33ouQ+9RRrjUmoU6RN9alX2kHNBaDS-6LkXt+Ep0hUEPFrgyJUcAJxaRwb7UmtcA7+i6YRQR8A3ERxelHX6scDGWG1m5TYoMkAJDAKsvsEANkACkIDigzhWfwzC1SuPDvEgRCMmjMgLr46YpdtKjGwHA1ZUA4AQDclASJdTglEXdmBKuLz9IfMoN835-zGmxMchTFyAArY5aBd7IvFGklAhImb+S-KvPuo4OaUAioPPWw82mCwqTAM2Etp75xgJAW2a9BYdM2i8nuzL6jotRUqfWzEvrguAJ8qF0AYWlIpSbSpY0JpZUss0xal8JX4ISTAF2Ic8UO3QTAblmKTqjLwbnUsarpnETmSIvRyyAZeCFZs7Z1r5SIGDLAYA2B3mEDyAUFxOcPGlkRlbNGvRjAmuBXCIOrs8EIqpiAbgeAFCuoxNGp1cbsDpNxZ2fFGtCWJtjXrMplKx6W3StbBQklJKSw5QSlqMaoCBgQFgveSqqVIyLRGAMpaGkaEyerNmWbq21rdKoBtzLJWFpRq2ktZa3ido1SG-0IzIDhUdXgMZcTlW3NVcHYNYdnqvT0UAA

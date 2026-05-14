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

Link to my diagram:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIHEEMC2MAK54E8BQWB2B7YGfAN0gCdoBlCs8gLmgCFwBXGcEXSAZ2nMgAHAT0i5g0ABIAVaSmjIePeAHNeAOgA6uWALHRO3PgOG8xEgOqQARlXwBjANaQJi5Wp45B8cqHshvcWpaCix4e2B8SjRMCgBGMIio6BiMCgAmRMjKAHlrUXI6BKwAXhKAGXwVLjKAYjTwcHwAdyw8gqKAWlrrNkgAPhpCinpK6txodpDyOOhvJWaogBMsVIzu3vZB6dGqrhT0NPJ0ufgF5dXD+I2+7eGGMf21mdPz8hXSkoRkaHsBeFA+FwdQaTVazziNy2Qzo9AAwv8iHAkJAEhCoQMYSMAFL4fbfVHQCySACS0gAopdYjNuio9Lg7rC4TkAHIs8lw6RYLHkWn0-oQ0Y5ACCABEAPqwYUAWUpz3SGMZI3KIB4wCpRwVPVuPPouPxKNmTHKwrhAGkNetanTIGIlQxmWyOVyeXzbQz5UKxZKZZTXdb+YKWTlpCSAGIkuHC0Os6AACgAVniJvLoL1wo4AJRtfLTSHa6E7FVqnMda4FzHTUt5t123WO9mc7nTWsMqb3OJeiVS2XN+6tgVXGb0YOhiNRmMs+NJ-btorQfC5+7Z-02u2e0fhyPRkmxxPJyZL+eLsvkbOfAlzQ4gyCNFqWmkB932+jS4Vm8ni6U5ABq5OgkDpJAAAsfZ0AOgrlCK3a+mBFAQUO6Rdj6varvyc7xMhPZ+i2T7rohI4hluE67lOcaIKQMCASBK64WubZHphm7jjue4UWQAFAcB55lJe9hAvYbA8CAQI3ne4KIQOuoAErklQJKwCycG8nhHpDp2zHbpO8YiCAKgpohtH9qpg7UkhmkkXuun6QcZlGeBJkYcOFmsWR1kGXZODylJRbksKf7KQh1IaURLHaXGUDwBx8r2fBjmMc5oVaaR8aRdFhk4NWHY+fcox+QFaHPkGSWWWRaUwE5cTnplQA

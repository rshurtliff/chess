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

Server Sequence Diagram:
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAHZM9qBACu2AMQALADMABwATACcIDD+yPYAFmA6CD6GAEoo9kiqFnJIEGiYiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAEQDlGjAALYo43XjMOMANCu46gDu0ByLy2srKLPASAj7KwC+mMK1MJWs7FyUDRNTUDPzF4fjm6o7UD2SxW63Gx1O52B42ubE43FgD1uogaUCyOTAlAAFJlsrlKJkAI5pXIAShuNVE9yqsnkShU6ga9hQYAAqoNMe9PigyTTFMo1KoqUYdHUAGJITgwNmUXkwHSWGCcuZiHSo4AAaylgxgWyQYASisGXJgwAQao4CpQAA90RpeXSBfdERSVA1pVBeeSRConVVbi8YAozShgBaOhr0ABRK0qbAEQpeu5lB4lcwNQJOYIjCbzdTAJmLFaRqDeeqG6bKk3B0MK+Tq9DQsycTD2-nqX3Vb0oBpoHwIBCJykPVv01R1EBqjHujmDXk87QO9sPYx1BQcDhamXaQc+4cLttjichjEKHz6zHAM8JOct-ejoUrtcb0-6z1I3cPWHPMs49H4tR9lgX7wh2-plm8RrKssDQHKCl76h0ED1mg0ErFciaUB2qYYA04ROE42aTJBXwwDBIIrPBCSIchqEHNc6AcKYXi+AE0DsEysSinAkbSHACgwAAMhA2RFNhzDOtQAYtO03R9AY6gFGgIwwKMSrzOsfwAhw1xgVSwEBhBFZfFCqy-Po-y7IWdGYPpUCVO+3YwAgwkSpiQkiYSxJgGSDlUiOAqMsy05qdyt60geVIrjA4qSu6sryuWHzKmFfL3smfouo5cXbr59mZQ0tRIAAZpYzTmVpmLZfIZKqiGmrujAaAQMwRW+JK0AwJpuwwBKIDQKi4A7oY6VgT2zUvgk0BIAAXigHDRrG8ZFLp6XwMgaZNBmACMXTKaMKy5qo+YLDBxalg0PgzFeU2zXsMD0c2DmVP5DIwEecgoBNF5XjeL2CsuIqBuugY-TlmW+rZDTuRKmSqIBNlPPCI01AZRFGSdaE-JR1ENmRyw6SjCKVGJDSNHhTg9HtaNJcZmNwVeOMoXj92mAxTHeH4-heCg6CxPESTc7z7m+FgYlCqNTTSJGAmRh0kY9L08mqIpIzY0h6AYQiFSQyp4yUTd+SFN8LO2VSDkNM59gi25wki55ajeUNfl3gFMBMmAX1q8h87hWlVRRTFz6g-IcoKl7Gu1RqIOvtoPWCr2-YwN9CHq2gjXNTArWXRwPupY6yNdj2fYDrleWSWWTUe9dUAzXNC0oHGima1h604ZtTg7TAVOHcdVlndAF1XfqN1zY2bN-b65vR9e25O89LuvRwKDcCeV7J1Rqe54u-3+4D0jL8yhgTZ6E8F1QyJz5UkMI3CmFny8murSTTTk5TYwPYxngcwEqIbv42ASk1AJdEMAADiyoNBiwkrUUmoDZYK3sMqVWDNU7N0-IjVG+sa7TUNkzGAAAecOaAKgwgwXZCoU9kC5HAbmdejMfKZXnr7V27tPYoO9ilbeD5AaB2nvFMO7CI5qk1MfWOORGrFyTkQ9OLU2o504RFM+yIJH9kvhQwmY0q7D2wXXGMDclpoJTK3MApNtpv11j3Asp0SwD0VEPSaOi9gfwUWlMu59XR8LBl2JhedXpULADQtQmIt6KN3g0XhpoEBgIgSfBe-03HKOAbkfBgTVC8gqENdKkMklgBSTE7QGTbL33AisRBuZCyNAmGUlAABJaQhYtrhGCIEUEWwEh6hQO6Lk+wfgpFAOqLpUElg-GqQAOSGeMaEMAugE3LulZ+ZN8K7TTvtcY1TVAVKqcqOpDSmktJWG0jpgzjKTNBH0kAAziInVOaU5U4yTlTJmU2T+zFOYcDcJEJwKAnCxEjMEOA3EABs8BJyGECTAYoxjxYaKaG0ToCCkH2MZtmMZypZlJiqDrCYWCZq4KskQkZdyJn4xvt+M2+U3qgsCZid6GJAn21JHPCof1ApaISHQzeLj85hOihKIOMcQ4JSIZgSOIjg7ADjioqJHLkIyMznIkJrj1GFylWoiWlcJoj3mnoxuCYVpGNKCY9undu4Cl7tY86dicW3THs2U+CSPGiPkEylltKUDUtRfMRV3KopPmifMWJzClzQOUdUupajHi3zLIEz0ptVoS1WWG+pDRGnNJZvqqoCzX57VufMHZKa9kszZl-Fi-hLDL2clsPmSBEhgHLf2CAVaABSEAJT+sMP4c56pIWGuheXUmzQWSyV6NU5BKdkLZmwAgYA5aoBwAgM5KA6wk3orvtrMhrwNjTtnfOxd+wADqLAaly16AAIQEgoOAABpQlebk0wFTfsvW1dcVLVQoQwRxDSFRvJSqgAVq2tA1KW0SnpSgIkDsGFdmdkGscbtmRsPHegb1S4eW8KdRKoVn6RXCM8SHcRCdpXSMrvK7OKH4nKvcY5QjaqYUapfbdeuurlqExboa0xHdzE5jNVYosNiyyXWtaPZx9qMoqowy6uJrKPXbOkOR7h4S+XttDjAcNorlMcAgGoNAAByZg1p0RcuDZR5RNHS6UYDGM5qoo5FMYMRmta7H24UxzeMSxGNxj9zLHoDcqIIO5BJePOJk8KUxtnup3dHU236jkG7Ss4jTTmlrBGFj0HikNCnTOyg4ZkJ2abg5hZGZghcYOjxjzXmGiJZDBaGAdYGxFseuDENHi-BaDpcqXkmIwvyA0tuygkWl0hSg1RmDvi4OtY+tSibGIODdeAPJgGdRMiZZ9HNozFHOxUbqGonWpKQLxphYYzNxi6iLJc+-VmzYS2cy8DO6ttbbsKkQCGWAwBsBTsILgiFUCxMwMltLWW8s+jGDQeuqNrxv1kodY5EA3A8DBMk7B8ccOoAI9PmEzIswIA0CrFEjD6w5vrAAGSbg9NuUT5sdsbr23fNxD8wLzJOy-JZe1nFAA
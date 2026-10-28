# MiniRedis

A small Redis-like **in-memory key-value database** written in Java, built as a case study for the Object Oriented Programming Lab.

It runs as a TCP server on port **6379** (the same port as real Redis). Several clients can connect at once, each served by its own thread, and all of them share one database.

```
127.0.0.1:6379> SET name Aveek
OK
127.0.0.1:6379> INCR visits
(integer) 1
127.0.0.1:6379> LPUSH tasks study code sleep
(integer) 3
127.0.0.1:6379> LRANGE tasks 0 -1
1) "sleep"
2) "code"
3) "study"
127.0.0.1:6379> INCR name
(error) ERR value is not an integer or out of range
```

## Architecture

```mermaid
flowchart LR
    subgraph Clients
        C1["RedisClient 1"]
        C2["RedisClient 2"]
    end

    subgraph Server["RedisServer (port 6379)"]
        direction TB
        SS["ServerSocket.accept()"] -->|new Thread per client| H1["ClientHandler 1"]
        SS --> H2["ClientHandler 2"]
        H1 --> E[CommandEngine]
        H2 --> E
        E -->|"synchronized (db)"| DB[(Database<br/>HashMap)]
        X[ExpiryCleaner thread<br/>every 1 s] -->|removeExpired| DB
    end

    C1 <-->|TCP text lines| H1
    C2 <-->|TCP text lines| H2
```

## Class diagram

```mermaid
classDiagram
    direction TB

    class RedisValue {
        <<abstract>>
        +type()* String
        +checkType(expected) void
    }
    class StringValue {
        #text : String
        +type() String
        +get() String
    }
    class CounterValue {
        -count : long
        +incr() long
        +get() String
    }
    class ListValue {
        -items : ArrayList~String~
        +type() String
        +pushFront(item) void
        +range(start, stop) ArrayList~String~
    }
    RedisValue <|-- StringValue
    StringValue <|-- CounterValue
    RedisValue <|-- ListValue

    class Command {
        <<abstract>>
        -name : String
        -minArgs : int
        -maxArgs : int
        +run(db, args) String
        #execute(db, args)* String
    }
    Command <|-- PingCommand
    Command <|-- SetCommand
    Command <|-- GetCommand
    Command <|-- DelCommand
    Command <|-- IncrCommand
    Command <|-- LPushCommand
    Command <|-- LRangeCommand
    Command <|-- KeysCommand
    Command <|-- ExpireCommand
    Command <|-- TtlCommand

    class RedisException {
        <<checked>>
    }
    Exception <|-- RedisException
    RedisException <|-- WrongTypeException
    RedisException <|-- UnknownCommandException
    RedisException <|-- InvalidArgumentException

    class Database {
        -data : HashMap~String, RedisValue~
        -expiresAt : HashMap~String, Long~
        +get(key) RedisValue
        +set(key, value) void
        +delete(key) boolean
        +expire(key, seconds) boolean
        +ttl(key) long
        +removeExpired() int
    }
    class CommandEngine {
        -commands : HashMap~String, Command~
        +execute(line) String
    }
    class ClientHandler {
        <<Runnable>>
        +run() void
    }
    class ExpiryCleaner {
        <<Thread>>
        +run() void
    }

    Database o-- RedisValue : stores
    CommandEngine o-- Command : registers
    CommandEngine --> Database : locks & uses
    ClientHandler --> CommandEngine
    ExpiryCleaner --> Database
    RedisServer ..> ClientHandler : starts threads
```

## What happens when you type a command

```mermaid
sequenceDiagram
    actor U as User
    participant C as RedisClient
    participant H as ClientHandler (thread)
    participant E as CommandEngine
    participant D as Database

    U->>C: INCR visits
    C->>H: "INCR visits\n" over socket
    H->>E: execute("INCR visits")
    E->>E: split, toUpperCase, find IncrCommand
    E->>D: synchronized (db) { run(...) }
    D-->>E: CounterValue → incr()
    E-->>H: "(integer) 11"
    H-->>C: reply + blank line
    C-->>U: (integer) 11
```

## Commands

| Command | Example | Reply |
|---|---|---|
| `PING [msg]` | `PING` | `PONG` |
| `SET key value` | `SET name Aveek` | `OK` |
| `GET key` | `GET name` | `"Aveek"` or `(nil)` |
| `DEL key [key ...]` | `DEL name tasks` | `(integer) 2` |
| `INCR key` | `INCR visits` | `(integer) 11` |
| `LPUSH key item [item ...]` | `LPUSH tasks study code` | `(integer) 2` |
| `LRANGE key start stop` | `LRANGE tasks 0 -1` | numbered list |
| `KEYS pattern` | `KEYS *`, `KEYS user*` | numbered list |
| `EXPIRE key seconds` | `EXPIRE otp 30` | `(integer) 1` |
| `TTL key` | `TTL otp` | seconds left, `-1` no expiry, `-2` missing |
| `QUIT` | `QUIT` | closes the connection |

## OOP concepts used

| Concept | Where |
|---|---|
| Abstraction + `final` method | `RedisValue.checkType()`, `Command.run()` |
| Three-level inheritance, `super`, `protected` | `RedisValue → StringValue → CounterValue` |
| Method overriding, superclass references | `CounterValue.get()`, `HashMap<String, Command>` holding every command |
| Custom checked exceptions, `throw`/`throws`, `finally` | `RedisException` family, `ClientHandler`, `RedisServer` |
| Strings and `StringBuffer` | parsing in `CommandEngine`, reply building in `Command.list()` |
| Arrays and `ArrayList` | `Arrays.sort` in `KeysCommand`, `ListValue` |
| Multithreading | thread per client, `ExpiryCleaner` daemon thread, `synchronized`, `join()` in `RaceDemo` |
| Networking | `ServerSocket` / `Socket` in `RedisServer` and `RedisClient` |

## Running it in BlueJ

1. **Project → Open Non BlueJ…** and open `MiniRedis-Server`. Run `RedisServer.main`.
2. Open `MiniRedis-Client` the same way (it gets its own terminal) and run `RedisClient.main`.
3. More clients can be started from a command prompt inside `MiniRedis-Client`:
   `"C:\Program Files\BlueJ\jdk\bin\java" -cp . RedisClient`
4. Stop the server with **Reset Java Virtual Machine**.

`RaceDemo.main` shows why synchronization matters: four threads each add 1 to a counter 100,000 times. Without a lock some updates are lost; through the synchronized engine the total is always exactly 400,000.

```
Expected total: 400000
Test 1 - without synchronization: 157551
Test 2 - with synchronization:    "400000"
```

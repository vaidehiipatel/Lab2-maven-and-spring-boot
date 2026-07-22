# Lab 2: Implementing and Testing Web Application and API Service using Apache Maven and Spring Boot

## What it does

Binary values are held as strings of `0` and `1` rather than as integers, so operand size is not bounded by Java's primitive types — a 200-digit binary number multiplies as cleanly as a 4-digit one.

Four operations are supported:

| Operator | Operation | Example |
|---|---|---|
| `+` | Addition | `111 + 1010` = `10001` |
| `*` | Multiplication | `111 * 1010` = `1000110` |
| `&` | Bitwise AND | `111 & 1010` = `10` |
| `\|` | Bitwise OR | `111 \| 1010` = `1111` |

Each is reachable two ways: through an HTML form in the browser, and through a REST endpoint that returns either a plain string or a JSON object.


## Requirements

- **JDK 8 or 11.** Spring Boot 2.1.2 will not build on Java 17 or newer.
- **Maven 3.x**

On macOS:

```bash
brew install maven
brew install --cask temurin@11
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

Verify with `java -version` — it must report 11.x before you build.

## Running

```bash
git clone <repository-url>
cd BinaryCalculatorWebapp
mvn clean install
mvn spring-boot:run
```

Then open http://localhost:8080

Stop the server with `Ctrl+C`.


## Testing

```bash
mvn clean test
```

**42 tests, 0 failures.**

| Suite | Tests | Covers |
|---|---|---|
| `BinaryControllerTest` | 16 | Web application |
| `BinaryAPIControllerTest` | 18 | REST API |
| `HelloControllerTest` | 2 | Introductory web section |
| `HelloAPIControllerTest` | 6 | Introductory API section |

Tests use Spring's `MockMvc`, which dispatches simulated HTTP requests through the controller layer without starting a server. Assertions cover status codes, view names, model attributes, response bodies, and JSON fields.

Each operator is tested against normal operation, zero operands, identity operands, and operands of unequal length. Multiplication additionally has a maximum-carry case (`1111 * 1111`) that stresses the carry chain inside the shift-and-add loop. The web application also covers failure paths — invalid operator, missing operator, and empty operand.

## API reference

All endpoints take `operand1` and `operand2` as query parameters. Missing parameters default to zero.

### Plain string responses

```
GET /add?operand1=111&operand2=1010          →  10001
GET /multiply?operand1=111&operand2=1010     →  1000110
GET /and?operand1=111&operand2=1010          →  10
GET /or?operand1=111&operand2=1010           →  1111
```

### JSON responses

```
GET /add_json?operand1=111&operand2=1010
GET /multiply_json?operand1=111&operand2=1010
GET /and_json?operand1=111&operand2=1010
GET /or_json?operand1=111&operand2=1010
```

Returns a `BinaryAPIResult`:

```json
{
  "operand1": "111",
  "operator": "multiply",
  "operand2": "1010",
  "result": "1000110"
}
```


## Project structure

```
BinaryCalculatorWebapp/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/ontariotechu/sofe3980U/
    │   │   ├── Application.java              Spring Boot entry point
    │   │   ├── Binary.java                   Binary arithmetic and bitwise logic
    │   │   ├── BinaryController.java         Web form handling
    │   │   ├── BinaryAPIController.java      REST endpoints
    │   │   ├── BinaryAPIResult.java          JSON response model
    │   │   ├── HelloController.java
    │   │   ├── HelloAPIController.java
    │   │   └── APIResult.java
    │   └── resources/templates/
    │       ├── calculator.html               Input form
    │       ├── result.html                   Result display
    │       ├── error.html                    Invalid operator
    │       └── hello.html
    └── test/java/com/ontariotechu/sofe3980U/
        ├── BinaryControllerTest.java
        ├── BinaryAPIControllerTest.java
        ├── HelloControllerTest.java
        └── HelloAPIControllerTest.java
```


## Conclusion

**`Binary.java`** stores the value as a normalized string. The constructor rejects any character outside `0` and `1` (falling back to `"0"`), strips leading zeros, and treats `null` and empty input as zero.

- **`add`** performs digit-by-digit addition from the least significant position, tracking carry.
- **`multiply`** uses shift-and-add: it scans the second operand right to left and, for each set bit, adds the first operand shifted left by the corresponding number of positions. Shifting left is implemented as appending a zero. Because it delegates to `add`, multiplication is correct wherever addition is.
- **`and`** and **`or`** iterate both operands from the least significant digit, treating exhausted positions as zero so that operands of different lengths align correctly. Results pass back through the constructor, which strips the leading zeros these operations can produce.

**`BinaryController`** switches on the submitted operator and dispatches to the matching `Binary` method, returning the result view. Unrecognized or missing operators fall through to the error view.

**`BinaryAPIController`** is a `@RestController`, so return values are serialized directly rather than resolved as view names.


# jlox

A lox compiler using java. Uses Gradle for building it. Learning Gradle and Java on the go.

## Prerequisites

- Java (I use `openjdk 11.0.20.1`)
- Gradle (optional ig, since `./gradlew` is available)

## Compile and Run

I am not sure what the proper way is, but I am using this janky way to run it from the jar file.

```bash
# build
./gradlew build

# run the interactive interpreter
java -cp lox/build/libs/lox.jar com.saivishnu.lox.lox

# or

# run the interpreter with a file containing the source code as input
java -cp lox/build/libs/lox.jar com.saivishnu.lox.Lox /path/to/file
```

## Some examples for getting an idea of the syntax

1. Expression
   - `-((1 + 2) / (1 \* 2));`
2. Variable declaration
   - `var a = 10;`
   - `var b = "abc";`
   - `var c = true;`
3. Print statement
   - `print "hello";`
   - `print 1 + 2;`
   - `print true;`
4. Exit interactive mode
   - `exit()`
   - Ctrl + C

## Link for the course

<a href="https://craftinginterpreters.com/" alt="CraftingInterpreters"> Crafting Interpreters </a>

## Project history

1. Started -> `30 August 2023, 19:48:20`
2. Discovered Gradle few hours later
3. Started another (current) repo with Gradle -> `31 August 2023, 20:54:35`
4. Expression analysis -> `10 September 2023, 22:08:55`
5. Visitors for the operators -> `14 September 2023, 19:48:35`
6. Parser -> `22 September 2023, 19:36:59`
7. Added Comments to the entire project -> `24 September 2023, 12:58:10`
8. Error handling -> `28 September 2023, 09:13:43`
9. Parsed expressions successfully -> `28 September 2023, 09:29:05`
10. Interpreter -> `28 September 2023, 14:25:24`
11. Runtime error handling -> `28 September 2023, 19:29:10`
12. Statements definition -> `29 September 2023, 08:38:57`
13. Interpret statements -> `29 September 2023, 12:02:12`

### Note

Delayed a lot due to exams in between. Might happen again in the future.

## License

[GNU General Public License v2.0](https://choosealicense.com/licenses/gpl-2.0/)

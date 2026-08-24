# UI test plan

This file is the source of truth for console UI tests. Each test case starts with
`## Test Case:` and contains **Aim**, **Command**, **Input**, and **Expected output**.
Commands are run from the project root. The test runner compares complete output.

## Test Case: Exits politely

- **Aim:** Verify that the chatbot starts and responds to the `bye` command with its farewell message.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
bye
```

- **Expected output**

```text
____________________________________________________________
 _   _       _
| \ | | ___ | |__
|  \| |/ _ \| '_ \
| |\  | (_) | |_) |
|_| \_|\___/|_.__/

  (•_•)
  ( •_•)>⌐■-■
  (⌐■_■)
Hii! I'm Nob :)
What's up?
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon!
____________________________________________________________
```

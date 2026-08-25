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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Handles invalid task numbers

- **Aim:** Verify that invalid task index input raises a clear error message instead of crashing or silently failing.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
todo borrow book
mark zebra
mark 1
list
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [T] borrow book [ ]
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
LET'S GOOO! I've marked this task as done:
  [T] borrow book [✓]
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T] borrow book [✓]
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects malformed deadline syntax without losing valid state

- **Aim:** Ensure a malformed deadline command is rejected while a later valid deadline remains in the task list.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
deadline return book /bySunday
deadline finish draft /by Friday
list
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Check that there is a space before and after '/by'.
Use: deadline DESCRIPTION /by DATE_OR_TIME
(eg., deadline return book /by Sunday)
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [D: Friday] finish draft [ ]
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D: Friday] finish draft [ ]
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects malformed event syntax without losing valid state

- **Aim:** Ensure a malformed event command is rejected while a later valid event remains in the task list.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
event project meeting /from Mon 2pm /to 4pm
event bad /from Mon 2pm
list
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [E: Mon 2pm to 4pm] project meeting [ ]
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Check that there is a space before and after '/from' and '/to'.
Use: event DESCRIPTION /from START /to END
(eg., event project meeting /from Mon 2pm /to 4pm)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E: Mon 2pm to 4pm] project meeting [ ]
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Handles empty todo and later valid todo without corrupting state

- **Aim:** Confirm an empty todo is rejected and a following valid todo still creates a task normally.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
todo
todo read book
list
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Use: todo DESCRIPTION
(eg., todo borrow book)
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [T] read book [ ]
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T] read book [ ]
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects empty deadline description

- **Aim:** Ensure a deadline without a description is rejected with a clear message telling the user the description must not be empty.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
deadline /by 2
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Description should not be empty.
Use: deadline DESCRIPTION /by DATE_OR_TIME
(eg., deadline return book /by Sunday)
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Rejects empty event description

- **Aim:** Ensure an event without a description is rejected with a clear message telling the user the description must not be empty.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
event /from Mon 2pm /to 4pm
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Description should not be empty.
Use: event DESCRIPTION /from START /to END
(eg., event project meeting /from Mon 2pm /to 4pm)
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

## Test Case: Deletes a task from the list

- **Aim:** Verify that `delete 2` removes the second task and shifts the remaining tasks left without affecting the rest of the task list.
- **Command**

```sh
javac -d out src/main/java/*.java && java -cp out Nob
```

- **Input**

```text
todo first task
todo second task
todo third task
delete 2
list
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

WASSUP! I'm Nob :)
How can I help you?
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [T] first task [ ]
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [T] second task [ ]
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Alrighty. I've added this task:
  [T] third task [ ]
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T] second task [ ]
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T] first task [ ]
2.[T] third task [ ]
____________________________________________________________
____________________________________________________________
Goodbye! Hope to see you soon mate!
____________________________________________________________
```

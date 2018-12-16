# Keys and Values Part 2

**Goal:** _Implement an in-memory key-value store with the specified API_

Continuing from the first part... 

# API requirements
## Add the ability to revert the kvPairs added in the last accept()

 ````java
 public interface KeysAndValues {
     void accept(String kvPairs);
     String display();

     void undo();
 }
 ````

### void undo()

 * reverts the current set of kvPairs to what it was before the last accept()
 * can be called multiple times

#### Examples
```java
KeysAndValues kv = new MyKeysAndValuesImplementation(listener);
kv.accept("one=1");
kv.accept("Three=four, one=5");

String before = kv.display();

kv.undo();

String after = kv.display();
````

`before` equals:
````text
one=6
Three=four
````

`after` equals:
````text
one=1
````

Author: WAN, Kwok Lung
Date: 2018-12-16

Technical Test Round 2
======================
Interfaces:
- /src/main/java/lung/key_value_store/api/ErrorListener [NO CHANGE]
- /src/main/java/lung/key_value_store/api/KeysAndValues [MODIFIED]
- /src/main/java/lung/key_value_store/api/UndoHistory [ADDED]
Implementation Class:
- /src/main/java/lung/key_value_store/KeysAndValuesImpl [MODIFIED]
- /src/main/java/lung/key_value_store/UndoHistoryImpl [ADDED]
Test Class:
- /src/test/java/lung/key_value_store/KeysAndValuesImplTest [MODIFIED]

This round is mainly to implement the undo() method. A new
class UndoHistoryImpl with its interface UndoHistory are created for this.

All modifications in the code are commented with "Added/Modified in Technical
Test Round 2";

In each accept() call, before the FIRST modification for each key, the value
of that key is saved into a temporary data-store (HashMap), so this temporary
data-store saves the current snapshot of all key-values that are modified in
this accept() call.

For example, let's assume the main data-store is currently {["one"]=["1"]}.
In a call of accept() for "Three=four, one=5", the current values of "Three"
and "one" will be saved into a snapshot (a temporary HashMap), so the snapshot
will store key-value pairs {["Three"]=[null],["one"]=["1"]}. As there is
previously no existing value of the key "Three" in the main data-store
(i.e. the key didn't appear in any previous accept() ), the snapshot will
store "null" as the value for that key. The snapshot (HashMap) will then be
converted to String[] before being saved into the Undo History.

The Undo History is implemented with an ArrayDeque<String[]> as a ring buffer.
Each snapshot is appended (LIFO) as an String[] element into the ArrayDeque.
The initial and also the maximum size of the Undo History (ArrayDeque) is
UNDO_HISTORY_SIZE (default value is 256) which also means the maximum number
of available consecutive undo() calls will be UNDO_HISTORY_SIZE. When the
ArrayDeque is empty, calling undo() will have no effect. When the ArrayDeque
is full, calling accept() will remove the first snapshot and append a new
snapshot at the end. The ArrayDeque size is checked before each adding, so
that removal of the first element is done when max size is reached. The
Undo History appends a new snapshot (each snapshot is a String[]) of the
changed key-values in each accept() call.

*** IMPORTANT ***
Please change KeysAndValuesImpl.UNDO_HISTORY_SIZE to fit your testing data
size.
As the question doesn't mention whether there is a upper limit size of the
Undo History, I presume it's better to have a max size to fit the real life
usage. ArrayDeque by nature would grow in size as necessary to support usage.
I suppress it from growing to mimic a ring buffer to implement a bounded
Undo History.
*** IMPORTANT ***

The snapshot is first built with a temporary and reusable HashMap (reusing to
avoid creating too much garbage), but it will then be converted to a String[]
before being stored in the Undo History. The snapshot can actually be saved,
for simplicity, directly as a HashMap into the Undo History but storing one
HashMap for each snapshot seems too bulky in memory. Another way is to convert
it to a LinkedList of tuples (pairs) but it's still bulky in memory as well.
Finally, I have chosen to convert the temporary HashMap to String[]. Its
memory footprint is smaller. It's a 1D array (well, it can be 2D but 1D is more
efficient in memory access). The data is stored like:
  {[key][value][key][value]...[key][value]} in a 1D String[] array.
So, a temporary HashMMap {["Three"]=[null],["one"]=["1"]} will be converted to:
  {["Three"][null]["one"]["1"]} in a 1D String[] array.

There are 6 new test cases added to test undo().

Discussion (another memory-efficient approach)
----------------------------------------------
The following discusses another memory-efficient approach which can decrease
the memory footprint in the program, but this approach is not implemented in
the program.

In order to make a garbage-free Undo History data-store, we can ultimately
use a 1D bounded circular char/byte array structure to store the Undo History.

(head of array)                               (tail of array)
[ ... Three=|one=1;Three=four|one=5;Three=five|one=10; ... ]
      ^ startPos                                      ^ endPos

startPos is the start position of the snapshots
endPos is the end position of the snapshots
"|" is the delimiter between key-value pairs
";" is the delimiter between different snapshots

In the above,
[Three=|one=1] (char sequence) is a snapshot.
[Three=four|one=5] (char sequence) is another snapshot.
[Three=five|one=10] (char sequence) is another snapshot.

"endPos" saves the end position so that new snapshots can be appended after this.
When calling undo(), the previous snapshot before the end position will be
read i.e. [Three=five|one=10] and then the end position will move forward.

The array is accessed circularly so endPos reaching the end of the array means
continuing accessing from the start of the array.

But, with this method, the max number of snapshots that can be stored will be
uncertain because the limit is the size of the buffer (byte array) and each
snapshot has different size. For example, if a snapshot is super large that
it occupies the whole Undo History array, there will be only one snapshot in
the whole array. On the other hand, if each snapshot is very small compared to
the byte array size, the whole byte array can store many snapshots.

Since this assessment's aim is not to write a garbage-free program, I do
not use this approach in order to avoid making mistakes/bugs made while
implementing this low level data structure. Moreover, KeysAndValuesImpl
still needs to receive String[] for snapshots, so even if I use this approach,
I will still need to convert the contents from byte array to String[].
Obviously, conversion from byte array to String[] will still generate a lot
of garbage. So, in order to make the undo() method garbage-free, there will
be a lot to modify/revamp in other parts (the part done in Technical Test
Round 1) of the program as well.










Technical Test Round 1
======================

Interfaces:
- /src/main/java/lung/key_value_store/api/ErrorListener
- /src/main/java/lung/key_value_store/api/KeysAndValues

Implementation Class:
- /src/main/java/lung/key_value_store/KeysAndValuesImpl

Test Class:
- /src/test/java/lung/key_value_store/KeysAndValuesImplTest

Some performance notes:
- In this program, I try to generate less garbage by reusing objects.
- HashMap size is initialized with EXPECTED_NUMBER_OF_UNIQUE_KEYS to avoid
  resizing and to improve hash function distribution with less duplicated values.
- Java 8 Stream is used.
- Methods are made shorter to favour JIT compiler.
- ++i is faster than i++
- A custom method isInteger(String) to check if a String represents an integer,
  not checking by Integer.parseInt for better performance.
- Make local variables as shortcuts of object data members that are frequently
  accessed in a method to avoid frequent address redirection.

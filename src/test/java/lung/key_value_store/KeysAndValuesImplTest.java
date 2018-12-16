package lung.key_value_store;

import lung.key_value_store.api.ErrorListener;
import lung.key_value_store.api.KeysAndValues;
import org.junit.Assert;
import org.junit.Test;

/**
 * Modified in Technical Test Round 2
 *
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public class KeysAndValuesImplTest {

    /**
     * Added in Technical Test Round 2
     */
    @Test
    public void givenExample1ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("one=1");
        kv.accept("Three=four, one=5");
        String before = kv.display();
        Assert.assertEquals("one=6\nThree=four", before);

        kv.undo();
        String after = kv.display();
        Assert.assertEquals("one=1", after);
    }

    /**
     * Added in Technical Test Round 2
     *
     * Cascaded accept() and undo().
     */
    @Test
    public void example1ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("1=1");
        kv.accept("A=B, 1=2");
        kv.accept("A=1, 1=A");
        String before = kv.display();
        Assert.assertEquals("1=A\nA=1", before);

        kv.undo();
        String after = kv.display();
        Assert.assertEquals("1=3\nA=B", after);

        kv.undo();
        after = kv.display();
        Assert.assertEquals("1=1", after);

        kv.accept("1=2");
        after = kv.display();
        Assert.assertEquals("1=3", after);

        kv.undo();
        after = kv.display();
        Assert.assertEquals("1=1", after);

        kv.undo();
        after = kv.display();
        Assert.assertEquals("", after);

        kv.undo();
        after = kv.display();
        Assert.assertEquals("", after);

        kv.accept("1=1");
        kv.accept("A=B, 1=2");
        kv.accept("A=1, 1=A");
        after = kv.display();
        Assert.assertEquals("1=A\nA=1", after);
    }

    /**
     * Added in Technical Test Round 2
     */
    @Test
    public void example3ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        String display1 = kv.display();
        kv.accept("14=15");
        String display2 = kv.display();
        kv.accept("A=B52");
        String display3 = kv.display();
        kv.accept("dry=D.R.Y.");
        String display4 = kv.display();
        kv.accept("14=7");
        String display5 = kv.display();
        kv.accept("14=4");
        String display6 = kv.display();
        kv.accept("dry=Don't Repeat Yourself");
        Assert.assertEquals("14=26\nA=B52\ndry=Don't Repeat Yourself", kv.display());

        kv.undo();
        Assert.assertEquals(display6, kv.display());
        kv.undo();
        Assert.assertEquals(display5, kv.display());
        kv.undo();
        Assert.assertEquals(display4, kv.display());
        kv.undo();
        Assert.assertEquals(display3, kv.display());
        kv.undo();
        Assert.assertEquals(display2, kv.display());
        kv.undo();
        Assert.assertEquals(display1, kv.display());
    }

    /**
     * Added in Technical Test Round 2
     *
     * Test atomic keys.
     */
    @Test
    public void example4ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        String display1 = kv.display();
        kv.accept("one=two");
        String display2 = kv.display();
        kv.accept("441=one,X=Y, 442=2,500=three");
        String display3 = kv.display();
        kv.accept("one=2");
        Assert.assertEquals("441=one\n442=2\n500=three\none=2\nX=Y", kv.display());

        kv.undo();
        Assert.assertEquals(display3, kv.display());
        kv.undo();
        Assert.assertEquals(display2, kv.display());
        kv.undo();
        Assert.assertEquals(display1, kv.display());
    }

    /**
     * Added in Technical Test Round 2
     *
     * Test accepting empty inputs and undoing them.
     */
    @Test
    public void example5ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("");
        kv.accept("");
        kv.accept("");
        Assert.assertEquals("", kv.display());

        kv.undo();
        Assert.assertEquals("", kv.display());
        kv.undo();
        Assert.assertEquals("", kv.display());
        kv.undo();
        Assert.assertEquals("", kv.display());

        /**
         * Undo History should already be empty now
         */
        kv.undo();
        Assert.assertEquals("", kv.display());
        kv.undo();
        Assert.assertEquals("", kv.display());
        kv.undo();
        Assert.assertEquals("", kv.display());
    }

    /**
     * Added in Technical Test Round 2
     *
     * Test undoing more times than the max Undo History size.
     */
    @Test
    public void example6ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("");
        Assert.assertEquals("", kv.display());

        /**
         * Test undoing more times than the Undo History size.
         */
        for (int i=1, max=KeysAndValuesImpl.UNDO_HISTORY_SIZE + 5; i<=max; ++i) {
            kv.undo();
            Assert.assertEquals("", kv.display());
        }

        /**
         * Check if undo() still works properly after undoing more times than
         * the Undo History size.
         */
        kv.accept("a=1");
        Assert.assertEquals("a=1", kv.display());

        kv.accept("a=2");
        Assert.assertEquals("a=3", kv.display());

        kv.undo();
        Assert.assertEquals("a=1", kv.display());

        kv.undo();
        Assert.assertEquals("", kv.display());
    }

    /**
     * Added in Technical Test Round 2
     *
     * Test exhausting the Undo History size.
     */
    @Test
    public void example7ForTechnicalTestRound2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);

        /**
         * Test doing accept() more than the Undo History size
         *
         * Assume UNDO_HISTORY_SIZE is 16.
         *
         * After each accept(), the data-store should become
         * > a=1
         * > a=2
         * > a=3
         * > a=4
         * > a=5
         * > a=6
         * ...
         * > a=19
         * > a=20
         * > a=21 (21 = UNDO_HISTORY_SIZE + 5)
         */
        for (int i=1, max=KeysAndValuesImpl.UNDO_HISTORY_SIZE + 5; i<=max; ++i) {
            kv.accept("a=1");
            Assert.assertEquals("a=" + i, kv.display());
        }
        /**
         * UNDO_HISTORY_SIZE + 5 = 21
         *
         * Now the data-store should be equal to
         * > a=21 (21 = UNDO_HISTORY_SIZE + 5)
         */

        /**
         * Test doing accept() more than the Undo History size
         *
         * Undo History should only store UNDO_HISTORY_SIZE of snapshots i.e.
         * > a=20
         * > a=19
         * > a=18
         * ...
         * > a=7
         * > a=6
         * > a=5
         *
         * So, the oldest snapshot is a=5.
         *
         * i will traverse from 1 to 21
         * (21 = UNDO_HISTORY_SIZE + 5)
         */
        for (int i=1, max=KeysAndValuesImpl.UNDO_HISTORY_SIZE + 5; i<=max; ++i) {
            kv.undo();
            if (i <= KeysAndValuesImpl.UNDO_HISTORY_SIZE) {
                /**
                 * The Undo History has a size of UNDO_HISTORY_SIZE, so
                 * it can undo() for UNDO_HISTORY_SIZE times and each undo()
                 * will change the data-store content.
                 *
                 * UNDO_HISTORY_SIZE is 16
                 *
                 * when i=1, a=20 (UNDO_HISTORY_SIZE + 5 - 1)
                 * when i=2, a=19 (UNDO_HISTORY_SIZE + 5 - 2)
                 * when i=3, a=18 (UNDO_HISTORY_SIZE + 5 - 3)
                 * ...
                 * when i=16, a=5 (UNDO_HISTORY_SIZE + 5 - 16)
                 */
                Assert.assertEquals("a=" + (KeysAndValuesImpl.UNDO_HISTORY_SIZE + 5 - i), kv.display());

            } else {
                /**
                 * Undo History has already been empty, so doing more undo()
                 * will not change the data-store.
                 *
                 * when i=17, a=5 (Undo History was empty)
                 * when i=18, a=5 (Undo History was empty)
                 * when i=19, a=5 (Undo History was empty)
                 * when i=20, a=5 (Undo History was empty)
                 * when i=21, a=5 (Undo History was empty)
                 */
                Assert.assertEquals("a=5", kv.display());
            }
        }
    }

    @Test
    public void givenExample1() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("pi=314159,hello=world");
        Assert.assertEquals("hello=world\npi=314159", kv.display());
    }

    @Test
    public void givenExample2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("14=15");
        kv.accept("A=B52");
        kv.accept("dry=D.R.Y.");
        kv.accept("14=7");
        kv.accept("14=4");
        kv.accept("dry=Don't Repeat Yourself");
        Assert.assertEquals("14=26\nA=B52\ndry=Don't Repeat Yourself", kv.display());
    }

    @Test
    public void givenExample3() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("14=15, 14=7,A=B52, 14 = 4, dry = Don't Repeat Yourself");
        Assert.assertEquals("14=26\nA=B52\ndry=Don't Repeat Yourself", kv.display());
    }

    @Test
    public void givenExample4() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("one=two");
        kv.accept("Three=four");
        kv.accept("5=6");
        kv.accept("14=X");
        Assert.assertEquals("14=X\n5=6\none=two\nThree=four", kv.display());
    }

    @Test
    public void givenExampleForAtomicGroup1() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("441=one,X=Y, 442=2,500=three");
        Assert.assertEquals("441=one\n442=2\n500=three\nX=Y", kv.display());
    }

    @Test
    public void givenExampleForAtomicGroup2() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("18=zzz,441=one,500=three,442=2,442= A,441 =3,35=D,500=ok  ");
        Assert.assertEquals("18=zzz\n35=D\n441=3\n442=A\n500=ok", kv.display());
    }

    @Test
    public void givenExampleForAtomicGroup3() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 442", msg);
                isOnErrorCalled[0] = true;
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("441=3,500=not ok,13=qwerty");
        Assert.assertEquals("13=qwerty", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void givenExampleForAtomicGroup4() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 441,500", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("500= three , 6 = 7 ,441= one,442=1,442=4");
        Assert.assertEquals("441=one\n442=1\n500=three\n6=7", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTest1() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("1=1");
        kv.accept("1=2,1=3");
        kv.accept("A=A    ,    A   =  C");
        kv.accept("A = B");
        kv.accept("B  =  1");
        kv.accept("B =  B");
        kv.accept(" C=C ");
        kv.accept("   C=1   ");
        kv.accept("C=2");
        Assert.assertEquals("1=6\nA=B\nB=B\nC=3", kv.display());
    }

    @Test
    public void customTestNotCallingAccept() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        Assert.assertEquals("", kv.display());
    }

    @Test
    public void customTestAcceptingEmpty() {
        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("");
        Assert.assertEquals("", kv.display());
    }

    @Test
    public void customTestInvalidFormat() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("Invalid format.", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("abc");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestInvalidFormat2() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("Invalid format.", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("abc==1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestIntegerValueOverflows() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.fail();
            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.assertEquals("The integer value overflows.", msg);
                Assert.assertNotNull(e);
                isOnErrorCalled[0] = true;

            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("A=0,A=" + Integer.MAX_VALUE + "0");
        Assert.assertEquals("A=0", kv.display());

        /**
         * To make sure ErrorListener.onError(String,Exception) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestForAtomicGroup1() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 441", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("442=1,500=1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestForAtomicGroup2() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 442", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("441=1,500=1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestForAtomicGroup3() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 500", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("441=1,442=1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestForAtomicGroup4() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 441,442", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("500=1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestForAtomicGroup5() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 441,500", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("442=1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }

    @Test
    public void customTestForAtomicGroup6() {
        /**
         * As a wrapper of a Boolean to be modified in the anonymous inner class.
         */
        final Boolean[] isOnErrorCalled = new Boolean[1];
        isOnErrorCalled[0] = false;

        final ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onError(String msg) {
                Assert.assertEquals("atomic group(441,442,500) missing 442,500", msg);
                isOnErrorCalled[0] = true;

            }

            @Override
            public void onError(String msg, Exception e) {
                Assert.fail();
            }
        };

        KeysAndValues kv = new KeysAndValuesImpl(errorListener);
        kv.accept("441=1");
        Assert.assertEquals("", kv.display());

        /**
         * To make sure ErrorListener.onError(String) has been called.
         */
        Assert.assertTrue(isOnErrorCalled[0]);
    }
}
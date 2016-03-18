package com.bluelinelabs.conductor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BackstackTests {

    private Backstack mBackstack;

    @Before
    public void setup() {
        mBackstack = new Backstack();
    }

    @Test
    public void testPush() {
        Assert.assertEquals(0, mBackstack.size());
        mBackstack.push(RouterTransaction.builder(new TestController()).build());
        Assert.assertEquals(1, mBackstack.size());
    }

    @Test
    public void testPop() {
        mBackstack.push(RouterTransaction.builder(new TestController()).build());
        mBackstack.push(RouterTransaction.builder(new TestController()).build());
        Assert.assertEquals(2, mBackstack.size());
        mBackstack.pop();
        Assert.assertEquals(1, mBackstack.size());
        mBackstack.pop();
        Assert.assertEquals(0, mBackstack.size());
    }

    @Test
    public void testPeek() {
        RouterTransaction transaction1 = RouterTransaction.builder(new TestController()).build();
        RouterTransaction transaction2 = RouterTransaction.builder(new TestController()).build();

        mBackstack.push(transaction1);
        Assert.assertEquals(transaction1, mBackstack.peek());

        mBackstack.push(transaction2);
        Assert.assertEquals(transaction2, mBackstack.peek());

        mBackstack.pop();
        Assert.assertEquals(transaction1, mBackstack.peek());
    }

    @Test
    public void testPopTo() {
        RouterTransaction transaction1 = RouterTransaction.builder(new TestController()).build();
        RouterTransaction transaction2 = RouterTransaction.builder(new TestController()).build();
        RouterTransaction transaction3 = RouterTransaction.builder(new TestController()).build();

        mBackstack.push(transaction1);
        mBackstack.push(transaction2);
        mBackstack.push(transaction3);

        Assert.assertEquals(3, mBackstack.size());

        mBackstack.popTo(transaction1);

        Assert.assertEquals(1, mBackstack.size());
        Assert.assertEquals(transaction1, mBackstack.peek());
    }
}

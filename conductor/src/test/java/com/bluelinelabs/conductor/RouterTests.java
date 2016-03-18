package com.bluelinelabs.conductor;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RouterTests {

    private Router mRouter;

    @Before
    public void setup() {
        Activity activity = Robolectric.buildActivity(TestActivity.class).create().get();
        mRouter = Conductor.attachRouter(activity, new FrameLayout(activity), null);
    }

    @Test
    public void testSetRoot() {
        String rootTag = "root";

        Controller rootController = new TestController();

        Assert.assertFalse(mRouter.hasRootController());

        mRouter.setRoot(rootController, rootTag);

        Assert.assertTrue(mRouter.hasRootController());

        Assert.assertEquals(rootController, mRouter.getControllerWithTag(rootTag));
    }

    @Test
    public void testGetByInstanceId() {
        Controller controller = new TestController();

        mRouter.pushController(RouterTransaction.builder(controller).build());

        Assert.assertEquals(mRouter.getControllerWithInstanceId(controller.getInstanceId()), controller);
        Assert.assertEquals(mRouter.getControllerWithInstanceId("fake id"), null);
    }

    @Test
    public void testGetByTag() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";

        Controller controller1 = new TestController();
        Controller controller2 = new TestController();

        mRouter.pushController(RouterTransaction.builder(controller1)
                .tag(controller1Tag)
                .build());

        mRouter.pushController(RouterTransaction.builder(controller2)
                .tag(controller2Tag)
                .build());

        Assert.assertEquals(mRouter.getControllerWithTag(controller1Tag), controller1);
        Assert.assertEquals(mRouter.getControllerWithTag(controller2Tag), controller2);
    }

    @Test
    public void testPushPopControllers() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";

        Controller controller1 = new TestController();
        Controller controller2 = new TestController();

        mRouter.pushController(RouterTransaction.builder(controller1)
                .tag(controller1Tag)
                .build());

        Assert.assertEquals(mRouter.getBackstackSize(), 1);

        mRouter.pushController(RouterTransaction.builder(controller2)
                .tag(controller2Tag)
                .build());

        Assert.assertEquals(mRouter.getBackstackSize(), 2);

        mRouter.popCurrentController();

        Assert.assertEquals(mRouter.getBackstackSize(), 1);

        Assert.assertEquals(mRouter.getControllerWithTag(controller1Tag), controller1);
        Assert.assertEquals(mRouter.getControllerWithTag(controller2Tag), null);

        mRouter.popCurrentController();

        Assert.assertEquals(mRouter.getBackstackSize(), 0);

        Assert.assertEquals(mRouter.getControllerWithTag(controller1Tag), null);
        Assert.assertEquals(mRouter.getControllerWithTag(controller2Tag), null);
    }

    @Test
    public void testPopToTag() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";
        String controller3Tag = "controller3";
        String controller4Tag = "controller4";

        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        Controller controller3 = new TestController();
        Controller controller4 = new TestController();

        mRouter.pushController(RouterTransaction.builder(controller1)
                .tag(controller1Tag)
                .build());

        mRouter.pushController(RouterTransaction.builder(controller2)
                .tag(controller2Tag)
                .build());

        mRouter.pushController(RouterTransaction.builder(controller3)
                .tag(controller3Tag)
                .build());

        mRouter.pushController(RouterTransaction.builder(controller4)
                .tag(controller4Tag)
                .build());

        mRouter.popToTag(controller2Tag);

        Assert.assertEquals(mRouter.getBackstackSize(), 2);
        Assert.assertEquals(mRouter.getControllerWithTag(controller1Tag), controller1);
        Assert.assertEquals(mRouter.getControllerWithTag(controller2Tag), controller2);
        Assert.assertEquals(mRouter.getControllerWithTag(controller3Tag), null);
        Assert.assertEquals(mRouter.getControllerWithTag(controller4Tag), null);
    }

    static class TestActivity extends Activity { }

    static class TestController extends Controller {

        public TestController() { }

        @NonNull
        @Override
        protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
            return new View(inflater.getContext());
        }

    }
}

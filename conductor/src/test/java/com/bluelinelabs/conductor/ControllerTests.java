package com.bluelinelabs.conductor;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Controller.LifecycleListener;
import com.bluelinelabs.conductor.Controller.RetainViewMode;
import com.bluelinelabs.conductor.ControllerChangeHandler.ControllerChangeCompletedListener;
import com.bluelinelabs.conductor.ControllerTransaction.ControllerChangeType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ControllerTests {

    private ActivityController<TestActivity> mActivityController;
    private Router mRouter;

    private int mChangeStartCalls;
    private int mChangeEndCalls;
    private int mBindViewCalls;
    private int mAttachCalls;
    private int mUnbindViewCalls;
    private int mDetachCalls;
    private int mDestroyCalls;

    @Before
    public void setup() {
        mActivityController = Robolectric.buildActivity(TestActivity.class).create();
        Activity activity = mActivityController.get();
        mRouter = Conductor.attachRouter(activity, new FrameLayout(activity), null);
        mRouter.setRoot(new TestController());

        mChangeStartCalls = 0;
        mChangeEndCalls = 0;
        mBindViewCalls = 0;
        mAttachCalls = 0;
        mUnbindViewCalls = 0;
        mDestroyCalls = 0;
        mDestroyCalls = 0;
    }

    @Test
    public void testNormalLifecycle() {
        Controller controller = new TestController();
        attachLifecycleListener(controller);

        assertCalls(0, 0, 0, 0, 0, 0, 0);
        mRouter.pushController(RouterTransaction.builder(controller)
                .pushChangeHandler(getPushHandler(0, 0, 0, 0, 0, 0, 0))
                .popChangeHandler(getPopHandler(1, 1, 1, 1, 0, 0, 0))
                .build()
        );

        assertCalls(1, 1, 1, 1, 0, 0, 0);

        mRouter.popCurrentController();

        Assert.assertNull(controller.getView());

        assertCalls(2, 2, 1, 1, 1, 1, 1);
    }

    @Test
    public void testLifecycleWithActivityDestroy() {
        Controller controller = new TestController();
        attachLifecycleListener(controller);

        assertCalls(0, 0, 0, 0, 0, 0, 0);
        mRouter.pushController(RouterTransaction.builder(controller)
                .pushChangeHandler(getPushHandler(0, 0, 0, 0, 0, 0, 0))
                .build()
        );

        assertCalls(1, 1, 1, 1, 0, 0, 0);

        mActivityController.pause();

        assertCalls(1, 1, 1, 1, 0, 0, 0);

        mActivityController.stop();

        assertCalls(1, 1, 1, 1, 0, 0, 0);

        mActivityController.destroy();

        assertCalls(1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void testChildLifecycle() {
        Controller parent = new TestController();
        mRouter.pushController(RouterTransaction.builder(parent)
                .pushChangeHandler(new ChangeHandler(new ChangeHandlerListener() {
                    @Override
                    public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                        container.addView(to);
                        ViewUtils.setAttached(to, true);
                        changeListener.onChangeCompleted();
                    }
                }))
                .build());

        Controller child = new TestController();
        attachLifecycleListener(child);

        assertCalls(0, 0, 0, 0, 0, 0, 0);

        parent.addChildController(ChildControllerTransaction.builder(child, TestController.VIEW_ID)
                .pushChangeHandler(getPushHandler(0, 0, 0, 0, 0, 0, 0))
                .popChangeHandler(getPopHandler(1, 1, 1, 1, 0, 0, 0))
                .build()
        );

        assertCalls(1, 1, 1, 1, 0, 0, 0);

        parent.removeChildController(child);

        assertCalls(2, 2, 1, 1, 1, 1, 1);
    }

    @Test
    public void testChildLifecycle2() {
        Controller parent = new TestController();
        mRouter.pushController(RouterTransaction.builder(parent)
                .pushChangeHandler(new ChangeHandler(new ChangeHandlerListener() {
                    @Override
                    public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                        container.addView(to);
                        ViewUtils.setAttached(to, true);
                        changeListener.onChangeCompleted();
                    }
                }))
                .popChangeHandler(new ChangeHandler(new ChangeHandlerListener() {
                    @Override
                    public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                        container.removeView(from);
                        ViewUtils.setAttached(from, false);
                        changeListener.onChangeCompleted();
                    }
                }))
                .build());

        Controller child = new TestController();
        attachLifecycleListener(child);

        assertCalls(0, 0, 0, 0, 0, 0, 0);

        parent.addChildController(ChildControllerTransaction.builder(child, TestController.VIEW_ID)
                .pushChangeHandler(getPushHandler(0, 0, 0, 0, 0, 0, 0))
                .popChangeHandler(getPopHandler(1, 1, 1, 1, 0, 0, 0))
                .build()
        );

        assertCalls(1, 1, 1, 1, 0, 0, 0);

        mRouter.popCurrentController();
        ViewUtils.setAttached(child.getView(), false);

        assertCalls(1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void testViewRetention() {
        Controller controller = new TestController();

        // Test View getting released w/ RELEASE_DETACH
        controller.setRetainViewMode(RetainViewMode.RELEASE_DETACH);
        Assert.assertNull(controller.getView());
        View view = controller.inflate(new FrameLayout(mRouter.getActivity()));
        Assert.assertNotNull(controller.getView());
        ViewUtils.setAttached(view, true);
        Assert.assertNotNull(controller.getView());
        ViewUtils.setAttached(view, false);
        Assert.assertNull(controller.getView());

        // Test View getting retained w/ RETAIN_DETACH
        controller.setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        view = controller.inflate(new FrameLayout(mRouter.getActivity()));
        Assert.assertNotNull(controller.getView());
        ViewUtils.setAttached(view, true);
        Assert.assertNotNull(controller.getView());
        ViewUtils.setAttached(view, false);
        Assert.assertNotNull(controller.getView());

        // Ensure re-setting RELEASE_DETACH releases
        controller.setRetainViewMode(RetainViewMode.RELEASE_DETACH);
        Assert.assertNull(controller.getView());
    }

    private ChangeHandler getPushHandler(final int changeStart, final int changeEnd, final int bindView, final int attach, final int unbindView, final int detach, final int destroy) {
        return new ChangeHandler(new ChangeHandlerListener() {
            @Override
            public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                assertCalls(changeStart + 1, changeEnd, bindView + 1, attach, unbindView, detach, destroy);
                container.addView(to);
                ViewUtils.setAttached(to, true);
                assertCalls(changeStart + 1, changeEnd, bindView + 1, attach + 1, unbindView, detach, destroy);
                changeListener.onChangeCompleted();
            }
        });
    }

    private ChangeHandler getPopHandler(final int changeStart, final int changeEnd, final int bindView, final int attach, final int unbindView, final int detach, final int destroy) {
        return new ChangeHandler(new ChangeHandlerListener() {
            @Override
            public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                assertCalls(changeStart + 1, changeEnd, bindView, attach, unbindView, detach, destroy);
                container.removeView(from);
                ViewUtils.setAttached(from, false);
                assertCalls(changeStart + 1, changeEnd, bindView, attach, unbindView + 1, detach + 1, destroy + 1);
                changeListener.onChangeCompleted();
            }
        });
    }

    private void assertCalls(int changeStart, int changeEnd, int bindView, int attach, int unbindView, int detach, int destroy) {
        Assert.assertEquals(changeStart, mChangeStartCalls);
        Assert.assertEquals(changeEnd, mChangeEndCalls);
        Assert.assertEquals(bindView, mBindViewCalls);
        Assert.assertEquals(attach, mAttachCalls);
        Assert.assertEquals(unbindView, mUnbindViewCalls);
        Assert.assertEquals(detach, mDetachCalls);
        Assert.assertEquals(destroy, mDestroyCalls);
    }

    private void attachLifecycleListener(Controller controller) {
        controller.addLifecycleListener(new LifecycleListener() {
            @Override
            public void onChangeStart(@NonNull Controller controller, @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
                mChangeStartCalls++;
            }

            @Override
            public void onChangeEnd(@NonNull Controller controller, @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
                mChangeEndCalls++;
            }

            @Override
            public void postBindView(@NonNull Controller controller, @NonNull View view) {
                mBindViewCalls++;
            }

            @Override
            public void postAttach(@NonNull Controller controller, @NonNull View view) {
                mAttachCalls++;
            }

            @Override
            public void postUnbindView(@NonNull Controller controller) {
                mUnbindViewCalls++;
            }

            @Override
            public void postDetach(@NonNull Controller controller, @NonNull View view) {
                mDetachCalls++;
            }

            @Override
            public void postDestroy(@NonNull Controller controller) {
                mDestroyCalls++;
            }
        });
    }

    interface ChangeHandlerListener {
        void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener);
    }

    public static class ChangeHandler extends ControllerChangeHandler {

        private ChangeHandlerListener mListener;

        public ChangeHandler() { }

        public ChangeHandler(ChangeHandlerListener listener) {
            mListener = listener;
        }

        @Override
        public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
            mListener.performChange(container, from, to, isPush, changeListener);
        }
    }
}

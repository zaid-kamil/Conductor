package com.bluelinelabs.conductor;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.IdRes;
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

    private Application mApplication;
    private ActivityController<TestActivity> mActivityController;
    private Router mRouter;

    private CallState mCurrentCallState;

    public void createActivityController(Bundle savedInstanceState) {
        mActivityController = Robolectric.buildActivity(TestActivity.class).create(savedInstanceState).withApplication(mApplication).start();

        @IdRes int containerId = 4;
        FrameLayout routerContainer = new FrameLayout(mActivityController.get());
        routerContainer.setId(containerId);

        mRouter = Conductor.attachRouter(mActivityController.get(), routerContainer, savedInstanceState);
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(new TestController());
        }
    }

    @Before
    public void setup() {
        mApplication = new Application();

        createActivityController(null);

        mCurrentCallState = new CallState();
    }

    @Test
    public void testNormalLifecycle() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);

        CallState expectedCallState = new CallState();

        assertCalls(expectedCallState, controller);
        mRouter.pushController(RouterTransaction.builder(controller)
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
                .popChangeHandler(getPopHandler(expectedCallState, controller))
                .build()
        );

        assertCalls(expectedCallState, controller);

        mRouter.popCurrentController();

        Assert.assertNull(controller.getView());

        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityDestroy() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);

        CallState expectedCallState = new CallState();

        assertCalls(expectedCallState, controller);
        mRouter.pushController(RouterTransaction.builder(controller)
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
                .build()
        );

        assertCalls(expectedCallState, controller);

        mActivityController.pause();

        assertCalls(expectedCallState, controller);

        mActivityController.stop();

        assertCalls(expectedCallState, controller);

        mActivityController.destroy();

        expectedCallState.detachCalls++;
        expectedCallState.destroyViewCalls++;
        expectedCallState.destroyCalls++;
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityConfigurationChange() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);

        CallState expectedCallState = new CallState();

        assertCalls(expectedCallState, controller);
        mRouter.pushController(RouterTransaction.builder(controller)
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
                .tag("root")
                .build()
        );

        assertCalls(expectedCallState, controller);

        mActivityController.get().isChangingConfigurations = true;

        Bundle bundle = new Bundle();
        mActivityController.saveInstanceState(bundle);

        expectedCallState.detachCalls++;
        expectedCallState.saveViewStateCalls++;
        expectedCallState.saveInstanceStateCalls++;
        assertCalls(expectedCallState, controller);

        mActivityController.pause();
        assertCalls(expectedCallState, controller);

        mActivityController.stop();
        assertCalls(expectedCallState, controller);

        mActivityController.destroy();
        expectedCallState.destroyViewCalls++;
        assertCalls(expectedCallState, controller);

        createActivityController(bundle);
        controller = (TestController)mRouter.getControllerWithTag("root");

        expectedCallState.restoreInstanceStateCalls++;
        expectedCallState.restoreViewStateCalls++;
        expectedCallState.changeStartCalls++;
        expectedCallState.changeEndCalls++;
        expectedCallState.createViewCalls++;

        // Lifecycle listener isn't attached during restore, grab the current views from the controller for this stuff...
        mCurrentCallState.restoreInstanceStateCalls = controller.currentCallState.restoreInstanceStateCalls;
        mCurrentCallState.restoreViewStateCalls = controller.currentCallState.restoreViewStateCalls;
        mCurrentCallState.changeStartCalls = controller.currentCallState.changeStartCalls;
        mCurrentCallState.changeEndCalls = controller.currentCallState.changeEndCalls;
        mCurrentCallState.createViewCalls = controller.currentCallState.createViewCalls;

        assertCalls(expectedCallState, controller);

        mActivityController.resume();
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityBackground() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);

        CallState expectedCallState = new CallState();

        assertCalls(expectedCallState, controller);
        mRouter.pushController(RouterTransaction.builder(controller)
                .pushChangeHandler(getPushHandler(expectedCallState, controller))
                .build()
        );

        assertCalls(expectedCallState, controller);

        mActivityController.pause();

        Bundle bundle = new Bundle();
        mActivityController.saveInstanceState(bundle);

        expectedCallState.detachCalls++;
        expectedCallState.saveInstanceStateCalls++;
        expectedCallState.saveViewStateCalls++;
        assertCalls(expectedCallState, controller);

        mActivityController.resume();
        expectedCallState.createViewCalls++;
        expectedCallState.restoreViewStateCalls++;
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

        TestController child = new TestController();
        attachLifecycleListener(child);

        CallState expectedCallState = new CallState();

        assertCalls(expectedCallState, child);

        parent.addChildController(ChildControllerTransaction.builder(child, TestController.VIEW_ID)
                .pushChangeHandler(getPushHandler(expectedCallState, child))
                .popChangeHandler(getPopHandler(expectedCallState, child))
                .build()
        );

        assertCalls(expectedCallState, child);

        parent.removeChildController(child);

        assertCalls(expectedCallState, child);
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

        TestController child = new TestController();
        attachLifecycleListener(child);

        CallState expectedCallState = new CallState();

        assertCalls(expectedCallState, child);

        parent.addChildController(ChildControllerTransaction.builder(child, TestController.VIEW_ID)
                .pushChangeHandler(getPushHandler(expectedCallState, child))
                .popChangeHandler(getPopHandler(expectedCallState, child))
                .build()
        );

        assertCalls(expectedCallState, child);

        mRouter.popCurrentController();
        ViewUtils.setAttached(child.getView(), false);

        expectedCallState.detachCalls++;
        expectedCallState.destroyViewCalls++;
        expectedCallState.destroyCalls++;
        assertCalls(expectedCallState, child);
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

    private ChangeHandler getPushHandler(final CallState expectedCallState, final TestController controller) {
        return new ChangeHandler(new ChangeHandlerListener() {
            @Override
            public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                expectedCallState.changeStartCalls++;
                expectedCallState.createViewCalls++;
                assertCalls(expectedCallState, controller);

                container.addView(to);
                ViewUtils.setAttached(to, true);

                expectedCallState.attachCalls++;
                assertCalls(expectedCallState, controller);

                changeListener.onChangeCompleted();

                expectedCallState.changeEndCalls++;
                assertCalls(expectedCallState, controller);
            }
        });
    }

    private ChangeHandler getPopHandler(final CallState expectedCallState, final TestController controller) {
        return new ChangeHandler(new ChangeHandlerListener() {
            @Override
            public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
                expectedCallState.changeStartCalls++;
                assertCalls(expectedCallState, controller);
                container.removeView(from);
                ViewUtils.setAttached(from, false);

                expectedCallState.destroyViewCalls++;
                expectedCallState.detachCalls++;
                expectedCallState.destroyCalls++;
                assertCalls(expectedCallState, controller);

                changeListener.onChangeCompleted();

                expectedCallState.changeEndCalls++;
                assertCalls(expectedCallState, controller);
            }
        });
    }

    private void assertCalls(CallState callState, TestController controller) {
        Assert.assertEquals("Expected call counts and controller call counts do not match.", callState, controller.currentCallState);
        Assert.assertEquals("Expected call counts and lifecycle call counts do not match.", callState, mCurrentCallState);
    }

    private void attachLifecycleListener(Controller controller) {
        controller.addLifecycleListener(new LifecycleListener() {
            @Override
            public void onChangeStart(@NonNull Controller controller, @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
                mCurrentCallState.changeStartCalls++;
            }

            @Override
            public void onChangeEnd(@NonNull Controller controller, @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
                mCurrentCallState.changeEndCalls++;
            }

            @Override
            public void postCreateView(@NonNull Controller controller, @NonNull View view) {
                mCurrentCallState.createViewCalls++;
            }

            @Override
            public void postAttach(@NonNull Controller controller, @NonNull View view) {
                mCurrentCallState.attachCalls++;
            }

            @Override
            public void postDestroyView(@NonNull Controller controller) {
                mCurrentCallState.destroyViewCalls++;
            }

            @Override
            public void postDetach(@NonNull Controller controller, @NonNull View view) {
                mCurrentCallState.detachCalls++;
            }

            @Override
            public void postDestroy(@NonNull Controller controller) {
                mCurrentCallState.destroyCalls++;
            }

            @Override
            public void onSaveInstanceState(@NonNull Controller controller, @NonNull Bundle outState) {
                mCurrentCallState.saveInstanceStateCalls++;
            }

            @Override
            public void onRestoreInstanceState(@NonNull Controller controller, @NonNull Bundle savedInstanceState) {
                mCurrentCallState.restoreInstanceStateCalls++;
            }

            @Override
            public void onSaveViewState(@NonNull Controller controller, @NonNull Bundle outState) {
                mCurrentCallState.saveViewStateCalls++;
            }

            @Override
            public void onRestoreViewState(@NonNull Controller controller, @NonNull Bundle savedViewState) {
                mCurrentCallState.restoreViewStateCalls++;
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

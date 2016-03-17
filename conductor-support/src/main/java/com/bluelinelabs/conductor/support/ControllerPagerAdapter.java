package com.bluelinelabs.conductor.support;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ChildControllerTransaction;
import com.bluelinelabs.conductor.Controller;

/**
 * An adapter for ViewPagers that will handle adding and removing Controllers
 */
public abstract class ControllerPagerAdapter extends PagerAdapter {

    private final Controller mHost;

    /**
     * Creates a new ControllerPagerAdapter using the passed host.
     */
    public ControllerPagerAdapter(Controller host) {
        mHost = host;
    }

    /**
     * Return the Controller associated with a specified position.
     */
    public abstract Controller getItem(int position);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final String name = makeControllerName(container.getId(), getItemId(position));

        Controller controller = mHost.getChildController(name);
        if (controller == null) {
            controller = getItem(position);

            mHost.addChildController(ChildControllerTransaction.builder(controller, container.getId())
                    .tag(name)
                    .build());
        }

        return controller;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mHost.removeChildController((Controller)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Controller)object).getView() == view;
    }

    public long getItemId(int position) {
        return position;
    }

    private static String makeControllerName(int viewId, long id) {
        return viewId + ":" + id;
    }

}
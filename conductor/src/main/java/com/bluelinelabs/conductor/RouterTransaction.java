package com.bluelinelabs.conductor;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * A {@link ControllerTransaction} implementation used for adding {@link Controller}s to a {@link Router}.
 */
public class RouterTransaction extends ControllerTransaction {

    private RouterTransaction(Builder builder) {
        super(builder);
    }

    RouterTransaction(@NonNull Bundle bundle) {
        super(bundle);
    }

    /**
     * Creates a new Builder
     *
     * @param controller The {@link Controller} to add to the {@link Router}
     */
    public static Builder builder(@NonNull Controller controller) {
        return new Builder(controller);
    }

    /**
     * A {@link ControllerTransaction.Builder} implementation used for adding {@link Controller}s to a {@link Router}.
     */
    public static class Builder extends ControllerTransaction.Builder<Builder> {

        Builder(@NonNull Controller controller) {
            super(controller);
        }

        /** Creates the transaction */
        public RouterTransaction build() {
            return new RouterTransaction(this);
        }

    }

}
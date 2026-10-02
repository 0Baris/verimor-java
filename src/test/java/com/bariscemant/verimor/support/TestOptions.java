package com.bariscemant.verimor.support;

import com.bariscemant.verimor.ClientOptions;

public final class TestOptions extends ClientOptions<TestOptions> {
    @Override
    protected TestOptions self() {
        return this;
    }
}

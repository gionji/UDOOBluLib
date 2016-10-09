package org.udoo.bluhomeexample.interfaces;

/**
 * Created by harlem88 on 09/10/16.
 */

public interface OnResult<T> {
    void onSuccess(T o);
    void onError(Throwable throwable);
}

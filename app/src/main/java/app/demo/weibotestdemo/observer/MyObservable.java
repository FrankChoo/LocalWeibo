package app.demo.weibotestdemo.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99538 on 2017/5/26.
 */

public class MyObservable<T> {

    List<MyObserver<T>> mList = new ArrayList<>();

    public synchronized void register(MyObserver<T> mObserver) {
        if (mObserver != null && !mList.contains(mObserver)) {
            mList.add(mObserver);
        }
    }

    public synchronized void unregister(MyObserver<T> mObserver) {
        mList.remove(mObserver);
    }

    public void notifyObservers(T data) {
        for (MyObserver<T> mObserver : mList) {
            mObserver.onUpdate(this, data);
        }
    }

    public interface MyObserver<T> {
        void onUpdate(MyObservable<T> myObservable, T data);
    }

}

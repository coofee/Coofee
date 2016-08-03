package com.coofee.component.bus;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by zhaocongying on 16/8/4.
 * <p/>
 * <p/>
 * http://blog.human-readable.net/2015/08/lightweight-event-bus.html
 */
public class RxBus<T> {

    private final Subject<T, T> subject;

    public RxBus() {
        this(PublishSubject.<T>create());
    }

    public RxBus(Subject<T, T> subject) {
        this.subject = new SerializedSubject<T, T>(subject);
    }

    public <E extends T> void post(E event) {
        subject.onNext(event);
    }

    public Observable<T> observe() {
        return subject;
    }

    public boolean hasObservers() {
        return subject.hasObservers();
    }

    public <E extends T> Observable<E> observeEvents(Class<E> eventClass) {
        return subject.ofType(eventClass);//pass only events of specified type, filter all other
    }

    public static <T> RxBus<T> createSimple() {
        return new RxBus<T>();//PublishSubject is created in constructor
    }

    public static <T> RxBus<T> createRepeating(int numberOfEventsToRepeat) {
        return new RxBus<T>(ReplaySubject.<T>createWithSize(numberOfEventsToRepeat));
    }

    public static <T> RxBus<T> createWithLatest() {
        return new RxBus<T>(BehaviorSubject.<T>create());
    }

    public static <E1, E2> Observable<Object> observeEvents(RxBus<Object> bus, Class<E1> class1, Class<E2> class2) {
        return Observable.merge(bus.observeEvents(class1), bus.observeEvents(class2));
    }

}
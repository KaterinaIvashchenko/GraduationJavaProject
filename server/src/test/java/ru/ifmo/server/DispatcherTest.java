package ru.ifmo.server;

/**
 * Created by vadim on 02.01.17.
 */
public class DispatcherTest implements Dispatcher{
    @Override
    public String dispatch(Request request, Response response) {
        return "dispatched";
    }
}

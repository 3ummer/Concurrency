public class NoEscape {
    private final EventListener eventListener;

    private NoEscape(EventSource eventSource) {
        eventListener = new EventListener() {
            @Override
            public void onEvent(Event e) {
                doSomething(e);
            }
        };

    }

    // factory pattern to solve "this" escape
    public static NoEscape newInstance(EventSource eventSource) {
        NoEscape escape = new NoEscape(eventSource);
        eventSource.registerListener(escape.eventListener);
        return escape;
    }

    public void doSomething(Event event) {

    }

    public class EventSource {
        public void registerListener(EventListener listener) {

        }
    }

    public interface EventListener {
        public void onEvent(Event e);

    }

    public interface Event {

    }
}

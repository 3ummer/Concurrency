import java.util.Date;

public class ThisEscape {

    private final int importantValue;

    public ThisEscape(EventSource source) {
        source.registerListener(new EventListener() {
            @Override
            public void onEvent(Event e) {
                // by calling doSomething, we actually passed the
                // incomplete thisEscape to EventListener
                doSomething(e);
            }
        });
        importantValue = 42;
    }

    public void doSomething(Event e) {
        if (importantValue != 42) {
            System.out.println("race condition detected at " + new Date());
        } else {
            System.out.println("no race condition");
        }
    }

    public static class EventSource {
        public ThisEscape.EventListener listener;
        public void registerListener(EventListener eventListener) {
            listener = eventListener;
        }
    }

    public interface EventListener {
        public void onEvent(Event e);
    }

    public static class Event {

    }
}

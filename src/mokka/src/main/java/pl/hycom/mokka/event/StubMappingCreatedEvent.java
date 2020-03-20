package pl.hycom.mokka.event;

/**
 * Event indicating that new MockConfiguration was created.
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingCreatedEvent extends StubMappingEvent {

    public StubMappingCreatedEvent(Object source, Long id) {
        super(source, id);
    }

}

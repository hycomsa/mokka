package pl.hycom.mokka.event;

/**
 * Event indicating that MockConfiguration was updated.
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingUpdatedEvent extends StubMappingEvent {

    public StubMappingUpdatedEvent(Object source, Long id) {
        super(source, id);
    }

}

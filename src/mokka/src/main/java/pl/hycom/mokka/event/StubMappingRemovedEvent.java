package pl.hycom.mokka.event;

/**
 * Event indicating that MockConfiguration was removed.
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingRemovedEvent extends StubMappingEvent {

    public StubMappingRemovedEvent(Object source, Long id) {
        super(source, id);
    }

}

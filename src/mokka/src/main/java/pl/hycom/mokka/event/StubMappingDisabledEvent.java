package pl.hycom.mokka.event;

/**
 * Event indicating that MockConfiguration has been disabled.
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingDisabledEvent extends StubMappingEvent {

    public StubMappingDisabledEvent(Object source, Long id) {
        super(source, id);
    }

}

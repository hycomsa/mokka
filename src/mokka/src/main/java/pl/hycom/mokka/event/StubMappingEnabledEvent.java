package pl.hycom.mokka.event;

/**
 * Event indicating that MockConfiguration has been enabled.
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingEnabledEvent extends StubMappingEvent {

    public StubMappingEnabledEvent(Object source, Long id) {
        super(source, id);
    }

}

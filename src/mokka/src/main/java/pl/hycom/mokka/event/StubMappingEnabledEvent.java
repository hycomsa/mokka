package pl.hycom.mokka.event;

/**
 * Event responsible for enabling stubmapping
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingEnabledEvent extends StubMappingEvent {

    public StubMappingEnabledEvent(Object source, Long id) {
        super(source, id);
    }

}

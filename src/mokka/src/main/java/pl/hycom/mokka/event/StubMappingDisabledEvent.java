package pl.hycom.mokka.event;

/**
 * Event responsible for disabling stubmapping
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingDisabledEvent extends StubMappingEvent {

    public StubMappingDisabledEvent(Object source, Long id) {
        super(source, id);
    }

}

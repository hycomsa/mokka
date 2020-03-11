package pl.hycom.mokka.event;

/**
 * Event responsible for remove stubmapping
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingRemoveEvent extends StubMappingEvent {

    public StubMappingRemoveEvent(Object source, Long id) {
        super(source, id);
    }

}

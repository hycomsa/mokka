package pl.hycom.mokka.event;

/**
 * Event responsible for update stubmapping
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingUpdateEvent extends StubMappingEvent {

    public StubMappingUpdateEvent(Object source, Long id) {
        super(source, id);
    }

}

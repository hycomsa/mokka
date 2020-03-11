package pl.hycom.mokka.event;

/**
 * Event responsible for adding stubmapping
 *
 * @author adam.misterski@hycom.pl
 */
public class StubMappingAddEvent extends StubMappingEvent {

    public StubMappingAddEvent(Object source, Long id) {
        super(source, id);
    }

}

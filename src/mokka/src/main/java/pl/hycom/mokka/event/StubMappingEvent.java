package pl.hycom.mokka.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Abstract class extends Application  {@link ApplicationEvent}
 *
 * Abstract for stubmapping events storing fields id and uuid
 *
 * @author adam.misterski@hycom.pl
 */
public abstract class StubMappingEvent extends ApplicationEvent {

    private UUID uuid;

    private Long id;

    public StubMappingEvent(Object source, Long id) {
        super(source);
        this.uuid = UUID.nameUUIDFromBytes(id.toString().getBytes());
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getId(){
        return id;
    }

}

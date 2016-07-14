package pl.hycom.mokka.emulator.mock;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface TrackChanges {

}

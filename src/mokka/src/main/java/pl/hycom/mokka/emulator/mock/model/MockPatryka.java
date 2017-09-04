package pl.hycom.mokka.emulator.mock.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.hycom.mokka.emulator.mock.TrackChanges;
import pl.hycom.mokka.web.json.View;

import java.io.Serializable;
import java.util.List;


public class MockPatryka  implements Serializable {

    @JsonView(View.General.class)
    @TrackChanges
    public List<MockConfiguration> mocks;

    @JsonView(View.General.class)
    @TrackChanges
    public boolean hasNext = false;
}

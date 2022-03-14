package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.data.GraphSectors.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void loadFromTest() {
        Path basePath = Path.of("lausanne");
        try {
            Graph.loadFrom(basePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.dita.dost;

import org.apache.tools.ant.BuildException;
import org.dita.dost.exception.DITAOTException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProcessorTest {

    @Rule
    public final TemporaryFolder tempDirGenerator = new TemporaryFolder();

    private Processor p;
    private File tempDir;

    @Before
    public void setUp() throws Exception {
        String ditaDir = System.getProperty("dita.dir");
        if (ditaDir == null) {
            ditaDir = new File("src" + File.separator + "main").getAbsolutePath();
        }
        final ProcessorFactory pf = ProcessorFactory.newInstance(new File(ditaDir));

        tempDir = tempDirGenerator.newFolder("tmp");
        pf.setTempDir(tempDir);
        p = pf.newProcessor("html5");
    }

    @Test
    public void testRunWithoutArgs() throws Exception {
        try {
            p.run();
            fail();
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void testRun() throws DITAOTException {
        final File mapFile;
        final File out;
        try {
            mapFile = new File(getClass().getClassLoader().getResource("ProcessorTest/test.ditamap").toURI());
            out = tempDirGenerator.newFolder("out");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        p.setInput(mapFile)
                .setOutput(out)
                .run();
    }


    @Test(expected = org.dita.dost.exception.DITAOTException.class)
    public void testBroken() throws DITAOTException {
        final File mapFile;
        final File out;
        try {
            mapFile = new File(getClass().getClassLoader().getResource("ProcessorTest/broken.dita").toURI());
            out = tempDirGenerator.newFolder("out");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            p.setInput(mapFile)
                    .setOutput(out)
                    .run();
        } catch (Exception e) {
            assertTrue(tempDir.exists());
            throw e;
        }
    }

    @Test(expected = org.dita.dost.exception.DITAOTException.class)
    public void testCleanTempOnFailure() throws DITAOTException {
        final File mapFile;
        final File out;
        try {
            mapFile = new File(getClass().getClassLoader().getResource("ProcessorTest/broken.dita").toURI());
            out = tempDirGenerator.newFolder("out");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            p.setInput(mapFile)
                    .setOutput(out)
                    .cleanOnFailure(false)
                    .run();
        } catch (BuildException e) {
            assertFalse(tempDir.exists());
            throw e;
        }
    }

}

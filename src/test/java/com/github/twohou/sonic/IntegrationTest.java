/*
 * This Java source file was generated by the Gradle "init" task.
 */
package com.github.twohou.sonic;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(IntegrationTest.class);

  @Rule
  public GenericContainer sonic =
      new GenericContainer<>("valeriansaliou/sonic:v1.2.3")
          .withClasspathResourceMapping("sonic.cfg", "/etc/sonic.cfg", BindMode.READ_ONLY)
          .withExposedPorts(1491);

  private final String collection = "messages";
  private final String bucket = "default";
  private IngestChannel ingest;
  private SearchChannel search;
  private ControlChannel control;

  @Before
  public void setUp() throws IOException {
    // init channels
    String address = sonic.getContainerIpAddress();
    Integer port = sonic.getMappedPort(1491);
    String password = "passwd";
    Integer connectionTimeout = 5000;
    Integer readTimeout = 5000;
    ChannelFactory factory =
        new ChannelFactory(address, port, password, connectionTimeout, readTimeout);
    ingest = factory.newIngestChannel();
    search = factory.newSearchChannel();
    control = factory.newControlChannel();

    // index
    ingest.ping();
    ingest.push(
        collection,
        bucket,
        "1",
        "MPs are starting to debate the process of voting on their preferred Brexit options, as Theresa May prepares to meet Tory backbenchers in an effort to win them over to her agreement.");
    ingest.push(
        collection,
        bucket,
        "2",
        "A shadowy group committed to ousting North Korea\"s leader Kim Jong-un has claimed it was behind a raid last month at the North Korean embassy in Spain.");
    ingest.push(
        collection,
        bucket,
        "3",
        "Meng Hongwei, the former Chinese head of Interpol, will be prosecuted in his home country for allegedly taking bribes, China\"s Communist Party says.");
    ingest.push(
        collection,
        bucket,
        "4",
        "A Chinese student who was violently kidnapped by a stun-gun toting gang of masked men in Canada has been found safe and well, police say.");

    // save to disk
    control.consolidate();
  }

  @After
  public void cleanUp() throws IOException {
    log.info("Cleaning up");
    ingest.flushc(collection);
    ingest.flushb(collection, bucket);
    ingest.flusho(collection, bucket, "1");
    Integer resp = ingest.count(collection);
    assertEquals(Integer.valueOf(0), resp);
    resp = ingest.count(collection, bucket);
    assertEquals(Integer.valueOf(0), resp);
    resp = ingest.count(collection, bucket, "1");
    assertEquals(Integer.valueOf(0), resp);

    ingest.quit();
    search.quit();
    control.quit();
  }

  @Test
  public void testIntegration() throws IOException {
    Integer resp = ingest.count(collection);
    log.info("Count collection: {}", resp);
    assertEquals(1, resp.intValue());
    resp = ingest.count(collection, bucket);
    assertEquals(53, resp.intValue());
    resp = ingest.count(collection, bucket, "1");
    assertEquals(16, resp.intValue());

    // search
    search.ping();

    List<String> responses = search.query(collection, bucket, "debate");
    assertEquals("1", responses.get(0));

    responses = search.query(collection, bucket, "Chinese");
    responses.sort(String::compareTo);
    assertEquals("3", responses.get(0));
    assertEquals("4", responses.get(1));

    responses = search.suggest(collection, bucket, "There");
    assertEquals("theresa", responses.get(0));

    responses = search.suggest(collection, bucket, "Hong");
    assertEquals("hongwei", responses.get(0));
  }
}

package com.github.twohou.sonic;

import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchChannel extends Channel {
  public SearchChannel(
      @NonNull String address,
      @NonNull Integer port,
      @NonNull String password,
      @NonNull Integer connectionTimeout,
      @NonNull Integer readTimeout)
      throws IOException {
    super(address, port, password, connectionTimeout, readTimeout);
    this.start(Mode.search);
  }

  public List<String> query(
      @NonNull String collection,
      @NonNull String bucket,
      @NonNull String terms,
      Integer limit,
      Integer offset)
      throws IOException {
    this.send(
        String.format(
            "%s %s %s \"%s\"%s%s",
            SearchType.QUERY.name(),
            collection,
            bucket,
            terms,
            limit != null ? String.format(" LIMIT(%d)", limit) : "",
            offset != null ? String.format(" OFFSET(%d)", offset) : ""));

    String queryId = assertPendingSearch();
    return assertSearchResults(SearchType.QUERY, queryId);
  }

  public List<String> query(
      @NonNull String collection, @NonNull String bucket, @NonNull String terms)
      throws IOException {
    return query(collection, bucket, terms, null, null);
  }

  public List<String> suggest(
      @NonNull String collection, @NonNull String bucket, @NonNull String word, Integer limit)
      throws IOException {
    this.send(
        String.format(
            "%s %s %s \"%s\"%s",
            SearchType.SUGGEST.name(),
            collection,
            bucket,
            word,
            limit != null ? String.format(" LIMIT(%d)", limit) : ""));

    String searchId = assertPendingSearch();
    return assertSearchResults(SearchType.SUGGEST, searchId);
  }

  public List<String> suggest(
      @NonNull String collection, @NonNull String bucket, @NonNull String word) throws IOException {
    return suggest(collection, bucket, word, null);
  }

  /**
   * Return pending search ID or throw an exception when the prompt message isn't expected.
   *
   * @return pending search ID
   */
  private String assertPendingSearch() throws IOException {
    String line = this.readLine();
    Matcher matcher = Pattern.compile("^PENDING ([a-zA-Z0-9]+)$").matcher(line);
    if (!matcher.find()) {
      throw new SonicException("unexpected prompt: " + line);
    }
    return matcher.group(1);
  }

  private List<String> assertSearchResults(SearchType searchType, String searchId)
      throws IOException {
    String line = this.readLine();
    Matcher matcher =
        Pattern.compile("^EVENT " + searchType.name() + " " + searchId + " (.+)?$").matcher(line);

    if (!matcher.find()) {
      throw new SonicException("Unexpected prompt: " + line);
    }

    if (matcher.groupCount() != 1 || matcher.group(1) == null) {
      return Collections.emptyList();
    }

    String[] searchResults = matcher.group(1).split(" ");
    return Arrays.asList(searchResults);
  }
}

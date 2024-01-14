package com.epam.microservice.parser;

import com.epam.microservice.model.ResourceMetadata;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class ResourceParser {

  private static final String NAME = "dc:title";
  private static final String ARTIST = "xmpDM:artist";
  private static final String ALBUM = "xmpDM:album";
  private static final String LENGTH = "xmpDM:duration";
  private static final String YEAR = "xmpDM:releaseDate";

  public ResourceMetadata getMetadata(InputStream inputStream)
      throws TikaException, IOException, SAXException {

    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext parseContext = new ParseContext();
    Parser parser = new Mp3Parser();

    parser.parse(inputStream, handler, metadata, parseContext);

    return ResourceMetadata.builder()
        .name(metadata.get(NAME))
        .artist(metadata.get(ARTIST))
        .album(metadata.get(ALBUM))
        .length(metadata.get(LENGTH))
        .year(metadata.get(YEAR))
        .build();
  }
}

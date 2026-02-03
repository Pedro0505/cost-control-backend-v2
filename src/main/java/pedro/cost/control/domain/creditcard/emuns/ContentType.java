package pedro.cost.control.domain.creditcard.emuns;

import org.springframework.http.MediaType;

public enum ContentType {

    JSON(MediaType.APPLICATION_JSON_VALUE),
    XML(MediaType.APPLICATION_XML_VALUE),
    TEXT(MediaType.TEXT_PLAIN_VALUE),
    HTML(MediaType.TEXT_HTML_VALUE),
    CSV("text/csv"),
    FORM_URLENCODED(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
    MULTIPART(MediaType.MULTIPART_FORM_DATA_VALUE),
    OCTET_STREAM(MediaType.APPLICATION_OCTET_STREAM_VALUE),
    PDF("application/pdf");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}

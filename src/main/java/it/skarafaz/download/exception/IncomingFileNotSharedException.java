package it.skarafaz.download.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class IncomingFileNotSharedException extends RuntimeException {
    private static final long serialVersionUID = 1329316844277873857L;

    public IncomingFileNotSharedException(Long id) {
        super(String.format("This file is not shared: %d", id));
    }
}

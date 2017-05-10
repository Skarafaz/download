package it.skarafaz.download.spring;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

public interface ExtendedMessageSource extends MessageSource {

    Map<String, String> getMessages(Locale locale);
}

package it.skarafaz.download.spring;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;

public class ExtendedResourceBundleMessageSource extends ResourceBundleMessageSource implements ExtendedMessageSource {
    private static final Logger logger = LoggerFactory.getLogger(ExtendedResourceBundleMessageSource.class);

    @Override
    public Map<String, String> getMessages(Locale locale) {
        Map<String, String> messages = new HashMap<>(0);

        for (String basename : getBasenameSet()) {
            ResourceBundle bundle = getResourceBundle(basename, locale);

            if (bundle != null) {
                Enumeration<String> keys = bundle.getKeys();

                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();

                    if (!messages.containsKey(key)) {
                        messages.put(key, bundle.getString(key));
                    } else {
                        logger.warn("Found duplicated message key {} for locale {}", key, locale);
                    }
                }
            }
        }

        return messages;
    }
}

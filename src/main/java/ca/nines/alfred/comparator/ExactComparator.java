package ca.nines.alfred.comparator;

import ca.nines.alfred.util.Settings;
import ca.nines.alfred.util.Text;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExactComparator implements Comparator  {

    final Map<String, String> text;

    final Logger logger;

    final int minLength;

    public ExactComparator() {
        logger = LoggerFactory.getLogger(getClass());
        text = new HashMap<>();
        minLength = Settings.getInstance().getInt("min_length");
    }

    public void add(String id, String content) {
        if(content.length() < minLength) {
            return;
        }
        text.put(id, content);
    }

    public void complete() {
        logger.info("EXT contains {} documents.", text.size());
    }

    public double compare(String srcId, String dstId) {
        String a = text.get(srcId);
        String b = text.get(dstId);

        if(a == null || b == null) {
            return 0;
        }

        if(a.equals(b)) {
            return 1.0;
        }
        return 0;
    }

}

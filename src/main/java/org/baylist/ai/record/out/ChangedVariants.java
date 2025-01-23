package org.baylist.ai.record.out;

import java.util.List;

public record ChangedVariants(List<String> originalVariants,
                              List<String> newVariants) {

}

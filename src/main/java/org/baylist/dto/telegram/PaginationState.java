package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class PaginationState {

	private int currentPage;
	private Map<Integer, List<String>> pages;
	private Long categoryId;
	private String categoryName;
}

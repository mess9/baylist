package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SelectedCategoryState {

	private List<String> categories;
	private List<String> selectedCategories;

}

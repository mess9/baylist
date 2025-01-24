package org.baylist.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.baylist.db.entity.Category;

import java.util.List;

@Data
@AllArgsConstructor
public class SelectedCategoryState {

	private List<Category> categories;
	private List<Category> selectedCategories;

}

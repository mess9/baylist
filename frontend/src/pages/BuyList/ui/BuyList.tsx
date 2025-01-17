import type { Component } from "solid-js";
import { For } from "solid-js";

import type { ICategory } from "/widgets/Category/ui/Category";
import Category from "/widgets/Category/ui/Category";

import classes from "./BuyList.module.css";
import { createStore } from "solid-js/store";

const BuyList: Component = () => {
	const [categories, setCategories] = createStore<ICategory[]>([
		{
			id: "c1",
			name: "Category 1",
			items: [
				{ id: "i1", name: "ogurec", order: 0, categoryId: "c1" },
				{ id: "i1", name: "pomidor", order: 0, categoryId: "c1" },
				{ id: "i1", name: "luk", order: 0, categoryId: "c1" },
			],
			order: 1,
		},
		{
			id: "c2",
			name: "Category 2",
			items: [
				{ id: "i1", name: "kartoshka", order: 0, categoryId: "c1" },
				{ id: "i1", name: "morkov", order: 0, categoryId: "c1" },
				{ id: "i1", name: "kapusta", order: 0, categoryId: "c1" },
			],
			order: 2,
		},
		{
			id: "c3",
			name: "Category 3",
			items: [
				{ id: "i1", name: "batat", order: 0, categoryId: "c1" },
				{ id: "i1", name: "chicken", order: 0, categoryId: "c1" },
				{ id: "i1", name: "pepper", order: 0, categoryId: "c1" },
			],
			order: 3,
		},
	]);


	return (
		<div class={classes["buy-list-container"]}  onDragOver={(e) => e.preventDefault()}>
			<ul class={classes["buy-list-ul"]}>
				<For each={categories}>
					{(category) => (
						<li>
							<Category {...category} />
						</li>
					)}
				</For>
			</ul>
		</div>
	);
};

export default BuyList;

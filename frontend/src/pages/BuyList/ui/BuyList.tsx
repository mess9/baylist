import type { Component, Setter } from "solid-js";
import type { SortableEvent } from "solid-sortablejs";
import Sortable from "solid-sortablejs";

import type { ICategory } from "/widgets/Category/ui/Category";
import Category from "/widgets/Category/ui/Category";

import classes from "./BuyList.module.css";
import { createStore } from "solid-js/store";
import type { IItem } from "/features/Item/ui/Item";
// import { createQuery } from "@tanstack/solid-query";

const BuyList: Component = () => {
	// const getCategories = createQuery(()=>({
			
	// 	}))
	const [categories, setCategories] = createStore<ICategory[]>([
		{
			id: "c1",
			name: "Category 1",
			items: [
				{
					id: `c1_${crypto.randomUUID()}`,
					name: "ogurec",
					order: 1,
					parentId: "c1",
				},
				{
					id: `c1_${crypto.randomUUID()}`,
					name: "pomidor",
					order: 2,
					parentId: "c1",
			
				},
				{
					id: `c1_${crypto.randomUUID()}`,
					name: "luk",
					order: 3,
					parentId: "c1",
			
				},
				{
					id: `c1_${crypto.randomUUID()}`,
					name: "ne_luk",
					order: 4,
					parentId: "c1",
			
				},
			],
			order: 1,
	
		},
		{
			id: "c2",
			name: "Category 2",
			items: [
				{
					id: `c2_${crypto.randomUUID()}`,
					name: "kartoshka",
					order: 1,
					parentId: "c2",
			
				},
				{
					id: `c2_${crypto.randomUUID()}`,
					name: "morkov",
					order: 2,
					parentId: "c2",
			
				},
				{
					id: `c2_${crypto.randomUUID()}`,
					name: "kapusta",
					order: 3,
					parentId: "c2",
			
				},
			],
			order: 2,
	
		},
		{
			id: "c3",
			name: "Category 3",
			items: [
				{
					id: `c3_${crypto.randomUUID()}`,
					name: "batat",
					order: 1,
					parentId: "c3",
			
				},
				{
					id: `c3_${crypto.randomUUID()}`,
					name: "chicken",
					order: 2,
					parentId: "c3",
			
				},
				{
					id: `c3_${crypto.randomUUID()}`,
					name: "pepper",
					order: 3,
					parentId: "c3",
			
				},
			],
			order: 3,
		},
	]);

	const createSetItemsForCategory = (parentId: string): Setter<IItem[]> => {
		return (updater) => {
			setCategories(
				(category) => category.id === parentId, // Найти категорию по ID
				"items", // Указать путь к items
				typeof updater === "function" ? updater : () => updater, // Обновить items
			);
		};
	};

	const handleMove = (e: SortableEvent, depth: number) => {
		const categoryOrderSetter = () => {
			setCategories(
				(categories) => (console.log(categories, "incOS"), categories.map((category, index) => ({
						...category,
						order: index + 1,
					}),
				)));
		};

		if (depth === 0) {
			categoryOrderSetter();
			return;
		}

		const fromList = e.from;

		const closestFromParent = fromList.closest("[data-id]") || null;

		if (!(closestFromParent instanceof HTMLElement)) return;

		const parentFromId = closestFromParent.dataset["id"] || null;

		if (!parentFromId) return;

		const itemOrderSetterFromList = (parentFromId: string) => {
			setCategories(
				(category) => category.id === parentFromId,
				"items",
				(items) => items.map((item, index) => ({
					...item,
					order: index + 1,
				}))						
			);	
		};

		const toList = e.to;

		if (fromList === toList ) {
			itemOrderSetterFromList(parentFromId);
			return;
		}

		const closestToParent = toList.closest("[data-id]") || null;

		if (!(closestToParent instanceof HTMLElement)) return;

		const parentToId = closestToParent.dataset["id"] || null;

		if (!parentToId) return;

		const parentIdSetter = (parentToId: string) => {
			setCategories(
				(category) => category.id === parentToId,
				"items",
				(items) => items.map((item) => ({
					...item,
					id: `${parentToId}_${item.id.split("_")[1]}`,
					parentId: parentToId
				})),				
			);
		}

		const itemOrderSetterToList = (parentToId: string) => {
			setCategories(
				(category) => category.id === parentToId,
				"items",
				(items) => items.map((item, index) => ({
					...item,
					order: index + 1,
				}))						
			);	
		};

		parentIdSetter(parentToId);
		itemOrderSetterFromList(parentFromId);
		itemOrderSetterToList(parentToId);
	};
	
	// createEffect(() => {
	// 	console.log(categories);

	// 	console.log(categories[0].items);
	// 	console.log(categories[1].items);
	// });

	return (
		<div
			class={classes["buy-list-container"]}
			onDragOver={(e) => e.preventDefault()}
		>
			<ul class={classes["buy-list-ul"]}>
				<Sortable
					idField={"id"}
					items={categories}
					setItems={setCategories}
					onEnd={(e) => handleMove(e, 0)}
				>
					{(category) => (
						<li>
							<Category
								{...category}
								setItems={createSetItemsForCategory}
								handleMove={handleMove}
							/>
						</li>
					)}
				</Sortable>
			</ul>
		</div>
	);
};

export default BuyList;

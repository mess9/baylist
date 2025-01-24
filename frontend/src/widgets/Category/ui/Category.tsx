import type { Component, Setter } from "solid-js";
import { createEffect, createSignal, mergeProps } from "solid-js";

import {
	IconEllipsisHorizontalSolid,
	IconPlusSolid,
} from "/app/assets/svg/icons";

import type { IItem } from "/features/Item/ui/Item";
import Item from "/features/Item/ui/Item";

import type { SortableEvent } from "solid-sortablejs";
import Sortable from "solid-sortablejs";

import classes from "./Category.module.css";
import classesItem from "/features/Item/ui/Item.module.css";


export interface ICategory {
	id: string;
	name: string;
	items: IItem[];
	order: number;
	delimiter?: "top" | "bottom";
}

interface ICategoryProps extends ICategory {
	setItems: (categoryId: string) => Setter<IItem[]>;
	handleMove: (event: SortableEvent, depth: number) => void;
}

const Category: Component<ICategoryProps> = (props) => {
	const [isExpand, setIsExpand] = createSignal(true);

	let ulRef: HTMLUListElement | undefined;


	const merge = mergeProps({ delimiter: "bottom" }, props);

	//TODO: МЕМОИЗИРОВАТЬ В ЗАВИСИМОСТИ ОТ КОНТЕНТА?
	const changeMaxHeight = () => {
		if (!ulRef) return;
		const ul = ulRef;
		const currentHeight = ul.scrollHeight;
		ul.style.maxHeight = isExpand() ? `${currentHeight}px` : "0";
	};

	const toggleVisibility = () => {
		if (!ulRef) return;
		setIsExpand(!isExpand());
		changeMaxHeight();
	};

	createEffect(() => {
		changeMaxHeight();
	});

	return (
		<section class={classes["category-section"]} >
			<div class={classes["category__header"]}>
				<label class={classes["category__drop-down-control-label"]}>
					<input
						class={classes["category__drop-down-checbox"]}
						type="checkbox"
						onChange={toggleVisibility}
					/>
					<h2>{merge.name}</h2>
				</label>
				<div class={classes["category__controls"]}>
					<button
						class={classes["category__add-item-button"]}
						type="button"
					>
						<IconPlusSolid />
					</button>
					<button
						class={classes["category__more-button"]}
						type="button"
					>
						<IconEllipsisHorizontalSolid />
					</button>
				</div>
			</div>
			<ul
				classList={{
					[classes["category-ul"]]: true,
				}}
				ref={ulRef}
			>
				<Sortable
					idField={"id"}
					items={merge.items}
					setItems={merge.setItems(merge.id)}
					group="bl1"
					handle={`.${classesItem["buy-item__dnd-button"]}`}
					onChange={(() => changeMaxHeight())}
					onEnd={(e) => merge.handleMove(e, 1)}
				>
					{(item) => (
						<li
							classList={{
								[classes["category__item-li--last"]]:
									item.order ===
									merge.items.reduce((max, item) => {
										return item.order > max
											? item.order
											: max;
									}, 0),
								[classes["category__item-li--delimeter-top"]]:
									merge.delimiter === "top",
								[classes[
									"category__item-li--delimeter-bottom"
								]]: merge.delimiter === "bottom",
							}}
						>
							<Item {...item} />
						</li>
					)}
				</Sortable>
			</ul>
		</section>
	);
};

export default Category;

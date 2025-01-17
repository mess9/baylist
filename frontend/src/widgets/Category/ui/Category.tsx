import type { Component } from "solid-js";
import { createSignal, For, mergeProps } from "solid-js";

import {
	IconEllipsisHorizontalSolid,
	IconPlusSolid,
} from "/app/assets/svg/icons";

import type { IItem } from "/features/Item/ui/Item";
import Item from "/features/Item/ui/Item";

import classes from "./Category.module.css";

export interface ICategory {
	id: string;
	name: string;
	items: IItem[];
	order: number;
	delimiter?: "top" | "bottom";
}

const Category: Component<ICategory> = (props) => {
	const [isExpand, setIsExpand] = createSignal(true);
	let ulRef: HTMLUListElement | undefined;

	const merge = mergeProps({delimiter: "bottom"}, props)

	//TODO: МЕМОИЗИРОВАТЬ В ЗАВИСИМОСТИ ОТ КОНТЕНТА?
	const toggleVisibility = () => {
		if (!ulRef) return;

		const ul = ulRef;
		const currentHeight = ul.scrollHeight;
		ul.style.maxHeight = isExpand() ? `${currentHeight}px` : "0";

		requestAnimationFrame(() => {
			ul.style.transition = "max-height 0.3s ease";
			ul.style.maxHeight = isExpand() ? `${currentHeight}px` : "0";
		});

		setIsExpand(!isExpand());
	};

	// Убираем transition после завершения аниPмации (для корректной работы с динамическим контентом)
	// TODO: Не уверен что нужно, когда будет динамика - !!!проверить!!
	const removeTransition = () => {
		if (ulRef) {
			ulRef.style.transition = "";
		}
	};


	return (
		<section class={classes["category-section"]}>
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
				onTransitionEnd={removeTransition}
				ref={ulRef}
			>
				<For each={merge.items}>
					{(item) => (
						<li
							classList={{
								[classes["buy-list__item-li"]]: true,
								[classes[
									"category__item-li--delimeter-top"
								]]: merge.delimiter === "top",
								[classes[
									"category__item-li--delimeter-bottom"
								]]: merge.delimiter === "bottom",
							}}
						>
							<Item {...item}/>
						</li>
					)}
				</For>
			</ul>
		</section>
	);
};

export default Category;
